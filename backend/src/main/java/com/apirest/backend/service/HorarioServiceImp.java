package com.apirest.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.dto.CrearHorarioDTO;
import com.apirest.backend.dto.HorarioRespuestaDTO;
import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Horario;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IHorarioRepository;
import com.apirest.backend.repository.IProfesorRepository;

@Service
public class HorarioServiceImp implements IHorarioService {

    @Autowired
    IHorarioRepository horarioRepository;

    @Autowired
    IClaseRepository claseRepository;

    @Autowired
    IProfesorRepository profesorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioRespuestaDTO> listarHorarios() {
        return horarioRepository.findAll().stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioRespuestaDTO> listarPorClase(Integer idClase) {
        buscarClase(idClase);
        return horarioRepository.findByIdClase_IdClase(idClase).stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    // Orden de bloqueos (siempre el mismo, para evitar interbloqueos): primero la CLASE y luego su PROFESOR.
    // Con la clase bloqueada, nadie puede cambiarle el profesor mientras se valida; con el profesor bloqueado,
    // dos altas o cambios de horario del mismo profesor se ejecutan una tras otra y la segunda ve lo que guardó
    // la primera (por eso READ_COMMITTED).
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public HorarioRespuestaDTO crearHorario(CrearHorarioDTO datos) {
        validarHoras(datos);
        Clase clase = buscarClaseBloqueada(datos.getIdClase());
        validarEstadoClase(clase);
        bloquearProfesor(clase);
        validarConflicto(clase, datos, null);

        Horario horario = new Horario();
        aplicarDatos(horario, clase, datos);
        return convertirRespuesta(horarioRepository.saveAndFlush(horario));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public HorarioRespuestaDTO actualizarHorario(Integer idHorario, CrearHorarioDTO datos) {
        validarHoras(datos);
        Horario horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un horario con el ID: " + idHorario));
        Clase clase = buscarClaseBloqueada(datos.getIdClase());
        validarEstadoClase(clase);
        bloquearProfesor(clase);
        validarConflicto(clase, datos, idHorario);

        aplicarDatos(horario, clase, datos);
        return convertirRespuesta(horarioRepository.saveAndFlush(horario));
    }

    @Override
    @Transactional
    public void eliminarHorario(Integer idHorario) {
        Horario horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un horario con el ID: " + idHorario));
        horarioRepository.delete(horario);
    }

    private Clase buscarClase(Integer idClase) {
        return claseRepository.findById(idClase)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una clase con el ID: " + idClase));
    }

    // Lee la clase bloqueando su fila: el profesor que se lee aquí ya no puede cambiar hasta el fin de la transacción
    private Clase buscarClaseBloqueada(Integer idClase) {
        return claseRepository.findByIdForUpdate(idClase)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una clase con el ID: " + idClase));
    }

    private void validarEstadoClase(Clase clase) {
        if (clase.getEstadoClase() != EstadoClase.PROGRAMADA
                && clase.getEstadoClase() != EstadoClase.EN_CURSO) {
            throw new IllegalArgumentException(
                    "Solo se pueden programar horarios para clases programadas o en curso.");
        }
    }

    private void validarHoras(CrearHorarioDTO datos) {
        if (!datos.getHoraFin().after(datos.getHoraInicio())) {
            throw new IllegalArgumentException(
                    "La hora de finalización debe ser posterior a la hora de inicio.");
        }
    }

    // Bloquea la fila del profesor de la clase destino hasta el fin de la transacción
    private void bloquearProfesor(Clase clase) {
        Integer idProfesor = clase.getIdProfesor().getIdProfesor();
        profesorRepository.findByIdForUpdate(idProfesor)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un profesor con el ID: " + idProfesor));
    }

    // La base de datos decide si hay cruce: una sola consulta filtrada por profesor, día y franja
    private void validarConflicto(Clase clase, CrearHorarioDTO datos, Integer idHorarioActual) {
        boolean hayCruce = horarioRepository.existeCruce(
                clase.getIdProfesor().getIdProfesor(),
                datos.getDiaSemana(),
                datos.getHoraInicio(),
                datos.getHoraFin(),
                idHorarioActual);

        if (hayCruce) {
            throw new RecursoDuplicadoException(
                    "El profesor ya tiene una clase programada en ese horario.");
        }
    }

    private void aplicarDatos(Horario horario, Clase clase, CrearHorarioDTO datos) {
        horario.setIdClase(clase);
        horario.setDiaSemana(datos.getDiaSemana());
        horario.setHoraInicio(datos.getHoraInicio());
        horario.setHoraFin(datos.getHoraFin());
    }

    private HorarioRespuestaDTO convertirRespuesta(Horario horario) {
        return new HorarioRespuestaDTO(
                horario.getIdHorario(),
                horario.getIdClase().getIdClase(),
                horario.getIdClase().getNombre(),
                horario.getIdClase().getIdPrograma().getNombrePrograma(),
                horario.getDiaSemana(),
                horario.getHoraInicio(),
                horario.getHoraFin());
    }
}
