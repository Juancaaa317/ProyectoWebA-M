package com.apirest.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.model.Programa;
import com.apirest.backend.repository.IProgramaRepository;

@Service
public class ProgramaServiceImp implements IProgramaService {

    @Autowired
    IProgramaRepository programaRepository;

    // Listar
    @Override
    public List<Programa> listarProgramas() {
        return programaRepository.findAll();
    }

    // Filtrar por estado
    // Filtrar por estado
    @Override
    public List<Programa> filtrarPorEstado(EstadoPrograma estadoPrograma) {
        return programaRepository.findByEstadoPrograma(estadoPrograma);
    }

    @Override
    public Programa obtenerProgramaPorId(Integer idPrograma) {
        return programaRepository.findById(idPrograma).orElse(null);
    }

    @Override
    public Programa guardarPrograma(Programa programa) {
        return programaRepository.save(programa);
    }

    @Override
    public Programa actualizarPrograma(Integer idPrograma, Programa programa) {

        Programa programaExistente = programaRepository.findById(idPrograma).orElse(null);

        if (programaExistente == null) {
            return null;
        }

        programaExistente.setNombrePrograma(programa.getNombrePrograma());
        programaExistente.setDescripcion(programa.getDescripcion());
        programaExistente.setEstadoPrograma(programa.getEstadoPrograma());

        return programaRepository.save(programaExistente);
    }

    // Eliminar programa
    // Eliminar programa
    @Override
    public String eliminarPrograma(Integer idPrograma) {

        if (!programaRepository.existsById(idPrograma)) {
            return "No existe un programa con el ID: " + idPrograma;
        }

        programaRepository.deleteById(idPrograma);

        return "Programa eliminado correctamente.";
    }

}
