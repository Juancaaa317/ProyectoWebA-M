package com.apirest.backend.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.EstadoPago;
import com.apirest.backend.dto.CrearPagoDTO;
import com.apirest.backend.dto.EstadoPagoDTO;
import com.apirest.backend.dto.PagoRespuestaDTO;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Archivo;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Pago;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IPagoRepository;

@Service
public class PagoServiceImp implements IPagoService {

    private static final String CARPETA_COMPROBANTES = "comprobantes";

    // Un pago en cualquiera de estos estados ya "ocupa" el mes
    private static final List<EstadoPago> ESTADOS_VIGENTES =
            List.of(EstadoPago.PENDIENTE, EstadoPago.APROBADO);

    @Autowired
    IPagoRepository pagoRepository;

    @Autowired
    IEstudianteRepository estudianteRepository;

    @Autowired
    IArchivoService archivoService;

    // Transaccional: si falla el guardado del pago, el archivo del disco se borra (lo hace ArchivoServiceImp).
    // Se bloquea la fila del estudiante: dos pagos simultáneos del mismo estudiante se ejecutan uno tras otro,
    // y el segundo ya ve el pago del primero (por eso READ_COMMITTED, igual que en las inscripciones).
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PagoRespuestaDTO crearPago(String correo, CrearPagoDTO datos) {
        Estudiante estudiante = estudianteRepository.findByCorreoForUpdate(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de estudiante asociado a la sesión."));

        String periodo = datos.getPeriodo() != null ? datos.getPeriodo() : YearMonth.now().toString();

        // Se valida antes de escribir el archivo para no guardar nada si el pago no procede
        if (pagoRepository.existsByIdEstudiante_IdEstudianteAndPeriodoAndEstadoIn(
                estudiante.getIdEstudiante(), periodo, ESTADOS_VIGENTES)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un pago pendiente o aprobado para el periodo " + periodo + ".");
        }

        Archivo comprobante = archivoService.registrar(correo, datos.getArchivo(), CARPETA_COMPROBANTES);

        Pago pago = new Pago();
        pago.setIdEstudiante(estudiante);
        pago.setIdArchivo(comprobante);
        pago.setMonto(datos.getMonto());
        pago.setPeriodo(periodo);
        pago.setFechaPago(LocalDate.now());
        pago.setEstado(EstadoPago.PENDIENTE);

        return aRespuesta(pagoRepository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoRespuestaDTO> listarMisPagos(String correo) {
        Estudiante estudiante = estudianteRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de estudiante asociado a la sesión."));

        return pagoRepository
                .findByIdEstudiante_IdEstudianteOrderByFechaPagoDescIdPagoDesc(estudiante.getIdEstudiante())
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoRespuestaDTO> listarPagos(EstadoPago estado, String periodo) {
        String periodoFiltro = periodo == null || periodo.isBlank() ? null : periodo.trim();
        if (periodoFiltro != null && !periodoFiltro.matches("^\\d{4}-(0[1-9]|1[0-2])$")) {
            throw new IllegalArgumentException("El periodo debe tener el formato AAAA-MM.");
        }

        return pagoRepository.buscar(estado, periodoFiltro)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    // Se bloquea la fila del pago: si dos administradores lo resuelven a la vez, el segundo espera,
    // ve el estado ya resuelto y recibe un error en lugar de sobrescribir la decisión del primero.
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PagoRespuestaDTO actualizarEstado(Integer idPago, EstadoPagoDTO datos) {
        Pago pago = pagoRepository.findByIdForUpdate(idPago)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un pago con el ID: " + idPago));

        // Solo se resuelve un pago pendiente: aprobar o rechazar
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new IllegalArgumentException(
                    "Solo se puede cambiar el estado de un pago pendiente.");
        }
        if (datos.getEstado() == EstadoPago.PENDIENTE) {
            throw new IllegalArgumentException(
                    "El pago solo puede pasar a APROBADO o RECHAZADO.");
        }

        String observacion = datos.getObservacion() == null ? null : datos.getObservacion().trim();
        if (observacion != null && observacion.isEmpty()) {
            observacion = null;
        }
        if (datos.getEstado() == EstadoPago.RECHAZADO && observacion == null) {
            throw new IllegalArgumentException("Debes indicar el motivo del rechazo en la observación.");
        }

        pago.setEstado(datos.getEstado());
        pago.setObservacion(observacion);
        return aRespuesta(pagoRepository.save(pago));
    }

    private PagoRespuestaDTO aRespuesta(Pago pago) {
        Archivo archivo = pago.getIdArchivo();
        return new PagoRespuestaDTO(
                pago.getIdPago(),
                pago.getIdEstudiante().getIdEstudiante(),
                pago.getIdEstudiante().getNombre(),
                pago.getMonto(),
                pago.getPeriodo(),
                pago.getFechaPago(),
                pago.getEstado(),
                pago.getObservacion(),
                archivo == null ? null : archivo.getIdArchivo(),
                archivo == null ? null : archivo.getNombreOriginal());
    }
}
