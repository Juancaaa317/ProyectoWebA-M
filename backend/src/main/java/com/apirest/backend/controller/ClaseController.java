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

import com.apirest.backend.dto.ClaseDTO;
import com.apirest.backend.dto.ClaseRespuestaDTO;
import com.apirest.backend.service.IClaseService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    @Autowired
    IClaseService claseService;

    @GetMapping
    public ResponseEntity<List<ClaseRespuestaDTO>> listarClases() {
        return ResponseEntity.ok(claseService.listarClases());
    }

    @GetMapping("/{idClase}")
    public ResponseEntity<ClaseRespuestaDTO> obtenerClase(
            @PathVariable Integer idClase) {
        return ResponseEntity.ok(claseService.obtenerClasePorId(idClase));
    }

    @PostMapping
    public ResponseEntity<ClaseRespuestaDTO> crearClase(
            @Valid @RequestBody ClaseDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claseService.crearClase(datos));
    }

    @PutMapping("/{idClase}")
    public ResponseEntity<ClaseRespuestaDTO> actualizarClase(
            @PathVariable Integer idClase,
            @Valid @RequestBody ClaseDTO datos) {
        return ResponseEntity.ok(claseService.actualizarClase(idClase, datos));
    }

    @DeleteMapping("/{idClase}")
    public ResponseEntity<Void> eliminarClase(@PathVariable Integer idClase) {
        claseService.eliminarClase(idClase);
        return ResponseEntity.noContent().build();
    }
}
