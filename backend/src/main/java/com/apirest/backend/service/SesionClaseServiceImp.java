package com.apirest.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

        LocalDate fecha = datos.getFecha().toLocalDate();
        DiaSemana diaFecha = convertirDia(fecha.getDayOfWeek());
        if (diaFecha != horario.getDiaSemana()) {
            throw new IllegalArgumentException(
                    "La fecha elegida no corresponde al día configurado en el horario.");
        }

        if (clase.getFechaInicio() != null
                && datos.getFecha().before(clase.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La sesión no puede ser anterior a la fecha de inicio de la clase.");
        }
        if (clase.getFechaFinalizacion() != null
                && datos.getFecha().after(clase.getFechaFinalizacion())) {
            throw new IllegalArgumentException(
                    "La sesión no puede ser posterior a la fecha final de la clase.");
        }

        if (sesionRepository.existsByIdClase_IdClaseAndFechaAndHoraInicio(
                clase.getIdClase(), datos.getFecha(), horario.getHoraInicio())) {
            throw new RecursoDuplicadoException(
                    "Ya existe una sesión para esa clase, fecha y hora.");
        }

        SesionClase sesion = new SesionClase();
        sesion.setIdClase(clase);
        sesion.setFecha(datos.getFecha());
        sesion.setHoraInicio(horario.getHoraInicio());
        sesion.setHoraFin(horario.getHoraFin());
        sesion.setEstadoSesion(EstadoSesion.PROGRAMADA);

        return convertirRespuesta(sesionRepository.saveAndFlush(sesion));
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
