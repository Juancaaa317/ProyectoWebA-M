package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.dto.ProgramaDTO;
import com.apirest.backend.model.Programa;
import com.apirest.backend.service.IProgramaService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/programas")
public class ProgramaController {

    @Autowired
    IProgramaService programaService;

    // Crear un programa
    @PostMapping
    public ResponseEntity<Programa> crearPrograma(
            @Valid @RequestBody ProgramaDTO datos) {

        Programa programa = new Programa();
        programa.setNombrePrograma(datos.getNombrePrograma().trim());
        programa.setDescripcion(datos.getDescripcion());
        programa.setEstadoPrograma(datos.getEstadoPrograma());

        Programa nuevoPrograma = programaService.guardarPrograma(programa);

        return new ResponseEntity<>(
                nuevoPrograma, HttpStatus.CREATED);
    }

    // Listar todos los programas
    @GetMapping
    public ResponseEntity<List<Programa>> listarProgramas() {

        List<Programa> programas = programaService.listarProgramas();

        return ResponseEntity.ok(programas);
    }

    // Filtrar programas por estado
    // Filtrar por estado
    @GetMapping("/estado/{estadoPrograma}")
    public ResponseEntity<List<Programa>> filtrarPorEstado(
            @PathVariable EstadoPrograma estadoPrograma) {

        List<Programa> programas = programaService.filtrarPorEstado(estadoPrograma);

        return ResponseEntity.ok(programas);
    }

    // Consultar un programa por su ID
    @GetMapping("/{idPrograma}")
    public ResponseEntity<Programa> obtenerProgramaPorId(
            @PathVariable Integer idPrograma) {

        return ResponseEntity.ok(programaService.obtenerProgramaPorId(idPrograma));
    }

    // Actualizar un programa
    @PutMapping("/{idPrograma}")
    public ResponseEntity<Programa> actualizarPrograma(
            @PathVariable Integer idPrograma,
            @Valid @RequestBody ProgramaDTO datos) {

        Programa cambios = new Programa();
        cambios.setNombrePrograma(datos.getNombrePrograma().trim());
        cambios.setDescripcion(datos.getDescripcion());
        cambios.setEstadoPrograma(datos.getEstadoPrograma());

        Programa programaActualizado =
                programaService.actualizarPrograma(idPrograma, cambios);

        return ResponseEntity.ok(programaActualizado);
    }

    // Eliminar un programa
    @DeleteMapping("/{idPrograma}")
    public ResponseEntity<Void> eliminarPrograma(
            @PathVariable Integer idPrograma) {

        programaService.eliminarPrograma(idPrograma);
        return ResponseEntity.noContent().build();
    }
}
