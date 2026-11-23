package com.apirest.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.DiaSemana;
import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.Enums.EstadoSesion;
import com.apirest.backend.dto.CrearSesionClaseDTO;
import com.apirest.backend.dto.SesionClaseRespuestaDTO;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Horario;
import com.apirest.backend.model.SesionClase;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IHorarioRepository;
import com.apirest.backend.repository.ISesionClaseRepository;

@Service
public class SesionClaseServiceImp implements ISesionClaseService {

    private static final String MENSAJE_SESION_DUPLICADA = "Ya existe una sesión para esa clase, fecha y hora.";

    // Nombre de la restricción UNIQUE (idClase, fecha, horaInicio) de la tabla sesion_clase
    private static final String RESTRICCION_SESION_UNICA = "uq_sesion_clase_fecha_hora";

    @Autowired
    ISesionClaseRepository sesionRepository;

    @Autowired
    IHorarioRepository horarioRepository;

    @Autowired
    IClaseRepository claseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SesionClaseRespuestaDTO> listarPorClase(Integer idClase) {
        if (!claseRepository.existsById(idClase)) {
            throw new RecursoNoEncontradoException(
                    "No se encontró una clase con el ID: " + idClase);
        }
        return sesionRepository.findByIdClase_IdClaseOrderByFechaAscHoraInicioAsc(idClase)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional
    public SesionClaseRespuestaDTO crearSesion(CrearSesionClaseDTO datos) {
        Horario horario = horarioRepository.findById(datos.getIdHorario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un horario con el ID: " + datos.getIdHorario()));

        Clase clase = horario.getIdClase();
        if (clase.getEstadoClase() != EstadoClase.PROGRAMADA
                && clase.getEstadoClase() != EstadoClase.EN_CURSO) {
            throw new IllegalArgumentException(
                    "No se pueden programar sesiones para una clase finalizada o cancelada.");
        }

        LocalDate fecha = datos.getFecha();
        DiaSemana diaFecha = convertirDia(fecha.getDayOfWeek());
        if (diaFecha != horario.getDiaSemana()) {
            throw new IllegalArgumentException(
                    "La fecha elegida no corresponde al día configurado en el horario.");
        }

        if (clase.getFechaInicio() != null
                && fecha.isBefore(clase.getFechaInicio().toLocalDate())) {
            throw new IllegalArgumentException(
                    "La sesión no puede ser anterior a la fecha de inicio de la clase.");
        }
        if (clase.getFechaFinalizacion() != null
                && fecha.isAfter(clase.getFechaFinalizacion().toLocalDate())) {
            throw new IllegalArgumentException(
                    "La sesión no puede ser posterior a la fecha final de la clase.");
        }

        // Comprobación previa: da el mensaje claro en el caso normal sin llegar a tocar la restricción de la BD
        if (sesionRepository.existsByIdClase_IdClaseAndFechaAndHoraInicio(
                clase.getIdClase(), Date.valueOf(fecha), horario.getHoraInicio())) {
            throw new RecursoDuplicadoException(MENSAJE_SESION_DUPLICADA);
        }

        SesionClase sesion = new SesionClase();
        sesion.setIdClase(clase);
        sesion.setFecha(Date.valueOf(fecha));
        sesion.setHoraInicio(horario.getHoraInicio());
        sesion.setHoraFin(horario.getHoraFin());
        sesion.setEstadoSesion(EstadoSesion.PROGRAMADA);

        try {
            return convertirRespuesta(sesionRepository.saveAndFlush(sesion));
        } catch (DataIntegrityViolationException e) {
            // Dos peticiones simultáneas pasan la comprobación previa; la restricción UNIQUE de la tabla
            // (idClase, fecha, horaInicio) deja pasar solo a una y rechaza a la otra con este mismo mensaje.
            // Cualquier otra violación de integridad (por ejemplo una clave foránea) se propaga sin cambiar su sentido.
            if (esViolacionDeSesionUnica(e)) {
                throw new RecursoDuplicadoException(MENSAJE_SESION_DUPLICADA);
            }
            throw e;
        }
    }

    // MySQL nombra la restricción en el mensaje: "Duplicate entry '...' for key 'sesion_clase.uq_sesion_clase_fecha_hora'"
    private boolean esViolacionDeSesionUnica(DataIntegrityViolationException e) {
        String mensaje = e.getMostSpecificCause().getMessage();
        return mensaje != null && mensaje.contains(RESTRICCION_SESION_UNICA);
    }

    @Override
    @Transactional
    public SesionClaseRespuestaDTO actualizarEstado(Integer idSesion, EstadoSesion estado) {
        SesionClase sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una sesión con el ID: " + idSesion));

        if (sesion.getEstadoSesion() != estado
                && sesion.getEstadoSesion() != EstadoSesion.PROGRAMADA) {
            throw new IllegalArgumentException(
                    "Una sesión realizada o cancelada no puede cambiar de estado.");
        }

        sesion.setEstadoSesion(estado);
        return convertirRespuesta(sesionRepository.save(sesion));
    }

    private DiaSemana convertirDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    private SesionClaseRespuestaDTO convertirRespuesta(SesionClase sesion) {
        return new SesionClaseRespuestaDTO(
                sesion.getIdSesion(),
                sesion.getIdClase().getIdClase(),
                sesion.getIdClase().getNombre(),
                sesion.getIdClase().getIdPrograma().getNombrePrograma(),
                sesion.getFecha(),
                sesion.getHoraInicio(),
                sesion.getHoraFin(),
                sesion.getEstadoSesion());
    }
}
