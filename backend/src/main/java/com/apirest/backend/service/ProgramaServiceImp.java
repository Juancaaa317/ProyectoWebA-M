package com.apirest.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Programa;
import com.apirest.backend.repository.IProgramaRepository;

@Service
public class ProgramaServiceImp implements IProgramaService {

    @Autowired
    IProgramaRepository programaRepository;

    // Listar todos los programas
    @Override
    public List<Programa> listarProgramas() {
        return programaRepository.findAll();
    }

    // Filtrar programas por estado
    @Override
    public List<Programa> filtrarPorEstado(EstadoPrograma estadoPrograma) {
        return programaRepository.findByEstadoPrograma(estadoPrograma);
    }

    // Obtener un programa por su ID
    @Override
    public Programa obtenerProgramaPorId(Integer idPrograma) {

        return programaRepository.findById(idPrograma)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un programa con el ID: " + idPrograma
                ));
    }

    // Guardar un nuevo programa
    @Override
    public Programa guardarPrograma(Programa programa) {

        // Comprobar que el nombre no esté registrado
        if (programa.getNombrePrograma() != null
                && programaRepository.existsByNombrePrograma(
                        programa.getNombrePrograma())) {

            throw new RecursoDuplicadoException(
                    "Ya existe un programa con el nombre: "
                    + programa.getNombrePrograma()
            );
        }

        // Asignar estado ACTIVO si no se proporcionó uno
        if (programa.getEstadoPrograma() == null) {
            programa.setEstadoPrograma(EstadoPrograma.ACTIVO);
        }

        return programaRepository.saveAndFlush(programa);
    }

    // Actualizar un programa existente
    @Override
    public Programa actualizarPrograma(Integer idPrograma, Programa programa) {

        // Buscar el programa que queremos actualizar
        Programa programaExistente = programaRepository.findById(idPrograma)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un programa con el ID: " + idPrograma
                ));

        // Comprobar si el nuevo nombre pertenece a otro programa
        if (programa.getNombrePrograma() != null
                && !programa.getNombrePrograma().isBlank()) {

            boolean nombreDuplicado =
                    programaRepository.existsByNombreProgramaAndIdProgramaNot(
                            programa.getNombrePrograma(),
                            idPrograma
                    );

            if (nombreDuplicado) {
                throw new RecursoDuplicadoException(
                        "Ya existe otro programa con el nombre: "
                        + programa.getNombrePrograma()
                );
            }

            programaExistente.setNombrePrograma(programa.getNombrePrograma());
        }

        // Actualizar descripción
        programaExistente.setDescripcion(programa.getDescripcion());

        // Actualizar estado solamente si se proporciona uno
        if (programa.getEstadoPrograma() != null) {
            programaExistente.setEstadoPrograma(programa.getEstadoPrograma());
        }

        return programaRepository.saveAndFlush(programaExistente);
    }

    // Eliminar un programa
    @Override
    public String eliminarPrograma(Integer idPrograma) {

        // Buscar el programa antes de eliminarlo
        Programa programaExistente = programaRepository.findById(idPrograma)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un programa con el ID: " + idPrograma
                ));

        programaRepository.delete(programaExistente);

        return "Programa eliminado correctamente.";
    }
}