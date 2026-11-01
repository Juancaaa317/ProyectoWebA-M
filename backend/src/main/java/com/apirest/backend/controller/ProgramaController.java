package com.apirest.backend.controller;

import java.util.List;

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
            @RequestBody Programa programa) {

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

        Programa programa = programaService.obtenerProgramaPorId(idPrograma);

        if (programa == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(programa);
    }

    // Actualizar un programa
    @PutMapping("/{idPrograma}")
    public ResponseEntity<Programa> actualizarPrograma(
            @PathVariable Integer idPrograma,
            @RequestBody Programa programa) {

        Programa programaActualizado = programaService.actualizarPrograma(idPrograma, programa);

        if (programaActualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(programaActualizado);
    }

    // Eliminar un programa
    @DeleteMapping("/{idPrograma}")
    public ResponseEntity<String> eliminarPrograma(
            @PathVariable Integer idPrograma) {

        String respuesta = programaService.eliminarPrograma(idPrograma);

        if (respuesta.startsWith("No existe")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(respuesta);
        }

        return ResponseEntity.ok(respuesta);
    }
}