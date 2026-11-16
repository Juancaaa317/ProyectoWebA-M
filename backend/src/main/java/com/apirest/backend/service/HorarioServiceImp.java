package com.apirest.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

@Service
public class HorarioServiceImp implements IHorarioService {

    @Autowired
    IHorarioRepository horarioRepository;

    @Autowired
    IClaseRepository claseRepository;

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

    @Override
    @Transactional
    public HorarioRespuestaDTO crearHorario(CrearHorarioDTO datos) {
        validarHoras(datos);
        Clase clase = buscarClase(datos.getIdClase());
        validarEstadoClase(clase);
        validarConflicto(clase, datos, null);

        Horario horario = new Horario();
        aplicarDatos(horario, clase, datos);
        return convertirRespuesta(horarioRepository.saveAndFlush(horario));
    }

    @Override
    @Transactional
    public HorarioRespuestaDTO actualizarHorario(Integer idHorario, CrearHorarioDTO datos) {
        validarHoras(datos);
        Horario horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un horario con el ID: " + idHorario));
        Clase clase = buscarClase(datos.getIdClase());
        validarEstadoClase(clase);
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

    private void validarConflicto(Clase clase, CrearHorarioDTO datos, Integer idHorarioActual) {
        boolean hayCruce = horarioRepository.findAll().stream()
                .filter(h -> !h.getIdHorario().equals(idHorarioActual))
                .filter(h -> h.getDiaSemana() == datos.getDiaSemana())
                .filter(h -> h.getIdClase().getIdProfesor().getIdProfesor()
                        .equals(clase.getIdProfesor().getIdProfesor()))
                .anyMatch(h -> datos.getHoraInicio().before(h.getHoraFin())
                        && datos.getHoraFin().after(h.getHoraInicio()));

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
