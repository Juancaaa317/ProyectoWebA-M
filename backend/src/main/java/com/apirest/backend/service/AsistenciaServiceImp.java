package com.apirest.backend.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.apirest.backend.Enums.EstadoInscripcion;
import com.apirest.backend.Enums.EstadoSesion;
import com.apirest.backend.dto.AsistenciaEstudianteDTO;
import com.apirest.backend.dto.AsistenciaSesionDTO;
import com.apirest.backend.dto.MarcarAsistenciaDTO;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Asistencia;
import com.apirest.backend.model.Inscripcion;
import com.apirest.backend.model.SesionClase;
import com.apirest.backend.repository.IAsistenciaRepository;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IInscripcionRepository;
import com.apirest.backend.repository.IProfesorRepository;
import com.apirest.backend.repository.ISesionClaseRepository;

@Service
public class AsistenciaServiceImp implements IAsistenciaService {

    @Autowired
    IAsistenciaRepository asistenciaRepository;

    @Autowired
    IInscripcionRepository inscripcionRepository;

    @Autowired
    ISesionClaseRepository sesionRepository;

    @Autowired
    IProfesorRepository profesorRepository;

    @Autowired
    IEstudianteRepository estudianteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaSesionDTO> listarPorSesion(
            Integer idSesion, String correo, boolean esAdministrador) {

        SesionClase sesion = buscarSesion(idSesion);
        if (!esAdministrador) {
            validarProfesorAsignado(sesion, correo);
        }

        Map<Integer, Asistencia> asistencias = asistenciaRepository
                .findByIdSesion_IdSesion(idSesion).stream()
                .collect(Collectors.toMap(
                        a -> a.getIdInscripcion().getIdInscripcion(),
                        Function.identity()));

        return inscripcionRepository
                .findByIdClase_IdClaseAndFechaInscripcionLessThanEqual(
                        sesion.getIdClase().getIdClase(), sesion.getFecha())
                .stream()
                .filter(inscripcion -> inscripcion.getEstadoInscripcion() == EstadoInscripcion.ACTIVA
                        || asistencias.containsKey(inscripcion.getIdInscripcion()))
                .map(inscripcion -> convertirParaSesion(inscripcion,
                        asistencias.get(inscripcion.getIdInscripcion())))
                .toList();
    }

    @Override
    @Transactional
    public AsistenciaSesionDTO marcarAsistencia(
            Integer idSesion, Integer idInscripcion,
            String correoProfesor, MarcarAsistenciaDTO datos) {

        SesionClase sesion = buscarSesion(idSesion);
        validarProfesorAsignado(sesion, correoProfesor);

        if (sesion.getEstadoSesion() == EstadoSesion.CANCELADA) {
            throw new IllegalArgumentException(
                    "No se puede tomar asistencia en una sesión cancelada.");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una inscripción con el ID: " + idInscripcion));

        if (!inscripcion.getIdClase().getIdClase()
                .equals(sesion.getIdClase().getIdClase())) {
            throw new IllegalArgumentException(
                    "La inscripción no pertenece a la clase de esta sesión.");
        }
        if (inscripcion.getEstadoInscripcion() != EstadoInscripcion.ACTIVA) {
            throw new IllegalArgumentException(
                    "Solo se puede registrar asistencia para inscripciones activas.");
        }
        if (inscripcion.getFechaInscripcion().after(sesion.getFecha())) {
            throw new IllegalArgumentException(
                    "No se puede registrar asistencia antes de la fecha de inscripción.");
        }

        Asistencia asistencia = asistenciaRepository
                .findByIdInscripcion_IdInscripcionAndIdSesion_IdSesion(
                        idInscripcion, idSesion)
                .orElseGet(Asistencia::new);

        asistencia.setIdInscripcion(inscripcion);
        asistencia.setIdSesion(sesion);
        asistencia.setEstado(datos.getEstado());
        asistencia.setObservaciones(normalizarObservacion(datos.getObservaciones()));

        return convertirParaSesion(inscripcion,
                asistenciaRepository.saveAndFlush(asistencia));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaEstudianteDTO> listarMisAsistencias(String correo) {
        estudianteRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de estudiante asociado a la sesión."));

        return asistenciaRepository.buscarPorCorreoEstudiante(correo).stream()
                .map(this::convertirParaEstudiante)
                .toList();
    }

    private SesionClase buscarSesion(Integer idSesion) {
        return sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una sesión con el ID: " + idSesion));
    }

    private void validarProfesorAsignado(SesionClase sesion, String correo) {
        var profesor = profesorRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de profesor asociado a la sesión."));

        if (!sesion.getIdClase().getIdProfesor().getIdProfesor()
                .equals(profesor.getIdProfesor())) {
            throw new AccessDeniedException(
                    "Solo el profesor asignado a la clase puede gestionar su asistencia.");
        }
    }

    private String normalizarObservacion(String observaciones) {
        if (observaciones == null || observaciones.isBlank()) {
            return null;
        }
        return observaciones.trim();
    }

    private AsistenciaSesionDTO convertirParaSesion(
            Inscripcion inscripcion, Asistencia asistencia) {
        return new AsistenciaSesionDTO(
                inscripcion.getIdInscripcion(),
                inscripcion.getIdEstudiante().getIdEstudiante(),
                inscripcion.getIdEstudiante().getNombre(),
                asistencia == null ? null : asistencia.getEstado(),
                asistencia == null ? null : asistencia.getObservaciones());
    }

    private AsistenciaEstudianteDTO convertirParaEstudiante(Asistencia asistencia) {
        return new AsistenciaEstudianteDTO(
                asistencia.getIdAsistencia(),
                asistencia.getIdSesion().getIdClase().getIdClase(),
                asistencia.getIdSesion().getIdClase().getNombre(),
                asistencia.getIdSesion().getIdClase().getIdPrograma().getNombrePrograma(),
                asistencia.getIdSesion().getFecha(),
                asistencia.getIdSesion().getHoraInicio(),
                asistencia.getIdSesion().getHoraFin(),
                asistencia.getEstado(),
                asistencia.getObservaciones());
    }
}
