package com.apirest.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.dto.ClaseDTO;
import com.apirest.backend.dto.ClaseRespuestaDTO;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Programa;
import com.apirest.backend.model.Profesor;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IProgramaRepository;
import com.apirest.backend.repository.IProfesorRepository;

@Service
public class ClaseServiceImp implements IClaseService {

    @Autowired
    IClaseRepository claseRepository;

    @Autowired
    IProfesorRepository profesorRepository;

    @Autowired
    IProgramaRepository programaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClaseRespuestaDTO> listarClases() {
        return claseRepository.findAll().stream()
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

    @Override
    @Transactional
    public ClaseRespuestaDTO actualizarClase(Integer idClase, ClaseDTO datos) {
        validarFechas(datos);

        Clase clase = buscarClase(idClase);
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
