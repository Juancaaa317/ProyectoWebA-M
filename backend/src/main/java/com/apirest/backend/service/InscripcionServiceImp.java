package com.apirest.backend.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.Enums.EstadoInscripcion;
import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.dto.CrearInscripcionDTO;
import com.apirest.backend.dto.InscripcionRespuestaDTO;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Inscripcion;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IInscripcionRepository;

@Service
public class InscripcionServiceImp implements IInscripcionService {

    private static final List<EstadoInscripcion> ESTADOS_QUE_RESERVAN_CUPO =
            List.of(EstadoInscripcion.PENDIENTE, EstadoInscripcion.ACTIVA);

    @Autowired
    IInscripcionRepository inscripcionRepository;

    @Autowired
    IEstudianteRepository estudianteRepository;

    @Autowired
    IClaseRepository claseRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InscripcionRespuestaDTO crearInscripcion(String correo, CrearInscripcionDTO datos) {
        Estudiante estudiante = estudianteRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de estudiante asociado a la sesión."));

        Clase clase = claseRepository.findByIdForUpdate(datos.getIdClase())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una clase con el ID: " + datos.getIdClase()));

        if (clase.getEstadoClase() != EstadoClase.PROGRAMADA) {
            throw new IllegalArgumentException(
                    "Solo puedes inscribirte en clases programadas.");
        }

        if (clase.getIdPrograma().getEstadoPrograma() != EstadoPrograma.ACTIVO) {
            throw new IllegalArgumentException(
                    "El programa de esta clase no está activo.");
        }

        long inscritos = inscripcionRepository.countByIdClase_IdClaseAndEstadoInscripcionIn(
                clase.getIdClase(), ESTADOS_QUE_RESERVAN_CUPO);

        if (inscritos >= clase.getCupoMaximo()) {
            throw new RecursoDuplicadoException(
                    "La clase ya alcanzó su cupo máximo.");
        }

        Inscripcion inscripcion = inscripcionRepository
                .findByIdEstudiante_IdEstudianteAndIdClase_IdClase(
                        estudiante.getIdEstudiante(), clase.getIdClase())
                .orElseGet(Inscripcion::new);

        if (inscripcion.getEstadoInscripcion() != null
                && inscripcion.getEstadoInscripcion() != EstadoInscripcion.CANCELADA) {
            throw new RecursoDuplicadoException(
                    "Ya tienes una inscripción vigente para esta clase.");
        }

        inscripcion.setIdEstudiante(estudiante);
        inscripcion.setIdClase(clase);
        inscripcion.setFechaInscripcion(Date.valueOf(LocalDate.now()));
        inscripcion.setEstadoInscripcion(EstadoInscripcion.PENDIENTE);

        return convertirRespuesta(inscripcionRepository.saveAndFlush(inscripcion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionRespuestaDTO> listarMisInscripciones(String correo) {
        Estudiante estudiante = estudianteRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de estudiante asociado a la sesión."));

        return inscripcionRepository
                .findByIdEstudiante_IdEstudianteOrderByFechaInscripcionDesc(
                        estudiante.getIdEstudiante())
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionRespuestaDTO> listarInscripciones() {
        return inscripcionRepository.findAll().stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InscripcionRespuestaDTO actualizarEstado(
            Integer idInscripcion, EstadoInscripcion nuevoEstado) {

        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una inscripción con el ID: " + idInscripcion));

        EstadoInscripcion estadoActual = inscripcion.getEstadoInscripcion();
        boolean mismoEstado = estadoActual == nuevoEstado;
        boolean puedeAprobarOCancelar = estadoActual == EstadoInscripcion.PENDIENTE
                && (nuevoEstado == EstadoInscripcion.ACTIVA
                        || nuevoEstado == EstadoInscripcion.CANCELADA);
        boolean puedeCancelar = estadoActual == EstadoInscripcion.ACTIVA
                && nuevoEstado == EstadoInscripcion.CANCELADA;
        boolean puedeReabrir = estadoActual == EstadoInscripcion.CANCELADA
                && nuevoEstado == EstadoInscripcion.PENDIENTE;

        if (!mismoEstado && !puedeAprobarOCancelar && !puedeCancelar && !puedeReabrir) {
            throw new IllegalArgumentException(
                    "Una inscripción cancelada solo puede reabrirse como pendiente; "
                    + "una inscripción pendiente puede aprobarse o cancelarse, "
                    + "y una activa solo puede cancelarse.");
        }

        if (puedeReabrir) {
            Clase clase = claseRepository.findByIdForUpdate(
                            inscripcion.getIdClase().getIdClase())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró la clase asociada a la inscripción."));
            long inscritos = inscripcionRepository
                    .countByIdClase_IdClaseAndEstadoInscripcionIn(
                            clase.getIdClase(), ESTADOS_QUE_RESERVAN_CUPO);

            if (inscritos >= clase.getCupoMaximo()) {
                throw new RecursoDuplicadoException(
                        "No se puede reabrir la inscripción porque la clase alcanzó su cupo máximo.");
            }
        }

        inscripcion.setEstadoInscripcion(nuevoEstado);
        return convertirRespuesta(inscripcionRepository.save(inscripcion));
    }

    private InscripcionRespuestaDTO convertirRespuesta(Inscripcion inscripcion) {
        return new InscripcionRespuestaDTO(
                inscripcion.getIdInscripcion(),
                inscripcion.getIdEstudiante().getIdEstudiante(),
                inscripcion.getIdEstudiante().getNombre(),
                inscripcion.getIdClase().getIdClase(),
                inscripcion.getIdClase().getNombre(),
                inscripcion.getIdClase().getIdPrograma().getNombrePrograma(),
                inscripcion.getFechaInscripcion(),
                inscripcion.getEstadoInscripcion());
    }
}
