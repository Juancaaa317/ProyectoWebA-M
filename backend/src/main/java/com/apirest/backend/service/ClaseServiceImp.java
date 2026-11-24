package com.apirest.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.dto.ClaseDTO;
import com.apirest.backend.dto.ClaseRespuestaDTO;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Horario;
import com.apirest.backend.model.Programa;
import com.apirest.backend.model.Profesor;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IHorarioRepository;
import com.apirest.backend.repository.IProgramaRepository;
import com.apirest.backend.repository.IProfesorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaseServiceImp implements IClaseService {

    private final IClaseRepository claseRepository;

    private final IProfesorRepository profesorRepository;

    private final IProgramaRepository programaRepository;

    private final IHorarioRepository horarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClaseRespuestaDTO> listarClases() {
        return claseRepository.findAll().stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaseRespuestaDTO> listarMisClases(String correoProfesor) {
        Profesor profesor = profesorRepository.findByIdUsuario_Correo(correoProfesor)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de profesor asociado a la sesión."));

        return claseRepository.findByIdProfesor_IdProfesorOrderByNombreAsc(profesor.getIdProfesor())
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClaseRespuestaDTO obtenerClasePorId(Integer idClase) {
        return convertirRespuesta(buscarClase(idClase));
    }

    @Override
    @Transactional
    public ClaseRespuestaDTO crearClase(ClaseDTO datos) {
        validarFechas(datos);

        Clase clase = new Clase();
        aplicarDatos(clase, datos);
        if (datos.getEstadoClase() == null) {
            clase.setEstadoClase(EstadoClase.PROGRAMADA);
        }

        return convertirRespuesta(claseRepository.save(clase));
    }

    // Orden de bloqueos (igual que en HorarioServiceImp): primero la CLASE y luego el PROFESOR nuevo.
    // Así, crear un horario de esta clase y cambiarle el profesor se ejecutan uno tras otro, y siempre
    // se valida contra el profesor definitivo (por eso READ_COMMITTED, para ver lo ya confirmado).
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClaseRespuestaDTO actualizarClase(Integer idClase, ClaseDTO datos) {
        validarFechas(datos);

        Clase clase = claseRepository.findByIdForUpdate(idClase)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una clase con el ID: " + idClase));

        if (!clase.getIdProfesor().getIdProfesor().equals(datos.getIdProfesor())) {
            validarHorariosConNuevoProfesor(clase, datos.getIdProfesor());
        }

        aplicarDatos(clase, datos);
        if (datos.getEstadoClase() != null) {
            clase.setEstadoClase(datos.getEstadoClase());
        }

        return convertirRespuesta(claseRepository.save(clase));
    }

    @Override
    @Transactional
    public void eliminarClase(Integer idClase) {
        claseRepository.delete(buscarClase(idClase));
    }

    private Clase buscarClase(Integer idClase) {
        return claseRepository.findById(idClase)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una clase con el ID: " + idClase));
    }

    // Al pasar la clase a otro profesor, ninguno de sus horarios puede solaparse con los que ese profesor ya tiene.
    // Los horarios de esta clase aún pertenecen al profesor anterior, así que no cuentan como cruce contra sí mismos.
    private void validarHorariosConNuevoProfesor(Clase clase, Integer idProfesorNuevo) {
        profesorRepository.findByIdForUpdate(idProfesorNuevo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un profesor con el ID: " + idProfesorNuevo));

        for (Horario horario : horarioRepository.findByIdClase_IdClase(clase.getIdClase())) {
            boolean hayCruce = horarioRepository.existeCruce(
                    idProfesorNuevo,
                    horario.getDiaSemana(),
                    horario.getHoraInicio(),
                    horario.getHoraFin(),
                    horario.getIdHorario());

            if (hayCruce) {
                throw new RecursoDuplicadoException(
                        "El profesor ya tiene una clase programada el " + horario.getDiaSemana()
                                + " de " + horario.getHoraInicio() + " a " + horario.getHoraFin() + ".");
            }
        }
    }

    private void aplicarDatos(Clase clase, ClaseDTO datos) {
        Profesor profesor = profesorRepository.findById(datos.getIdProfesor())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un profesor con el ID: " + datos.getIdProfesor()));

        Programa programa = programaRepository.findById(datos.getIdPrograma())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un programa con el ID: " + datos.getIdPrograma()));

        clase.setIdProfesor(profesor);
        clase.setIdPrograma(programa);
        clase.setFechaInicio(datos.getFechaInicio());
        clase.setFechaFinalizacion(datos.getFechaFinalizacion());
        clase.setNombre(datos.getNombre().trim());
        clase.setCupoMaximo(datos.getCupoMaximo());
        if (datos.getEstadoClase() != null) {
            clase.setEstadoClase(datos.getEstadoClase());
        }
    }

    private void validarFechas(ClaseDTO datos) {
        if (datos.getFechaInicio() != null
                && datos.getFechaFinalizacion() != null
                && datos.getFechaFinalizacion().before(datos.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la fecha de inicio.");
        }
    }

    private ClaseRespuestaDTO convertirRespuesta(Clase clase) {
        return new ClaseRespuestaDTO(
                clase.getIdClase(),
                clase.getIdProfesor().getIdProfesor(),
                clase.getIdProfesor().getNombre(),
                clase.getIdPrograma().getIdPrograma(),
                clase.getIdPrograma().getNombrePrograma(),
                clase.getFechaInicio(),
                clase.getFechaFinalizacion(),
                clase.getNombre(),
                clase.getCupoMaximo(),
                clase.getEstadoClase());
    }
}
