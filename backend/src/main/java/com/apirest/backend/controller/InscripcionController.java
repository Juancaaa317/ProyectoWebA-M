package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.CrearInscripcionDTO;
import com.apirest.backend.dto.EstadoInscripcionDTO;
import com.apirest.backend.dto.InscripcionRespuestaDTO;
import com.apirest.backend.service.IInscripcionService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    IInscripcionService inscripcionService;

    @PostMapping
    public ResponseEntity<InscripcionRespuestaDTO> crearInscripcion(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CrearInscripcionDTO datos) {

        InscripcionRespuestaDTO respuesta =
                inscripcionService.crearInscripcion(jwt.getSubject(), datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/mias")
    public ResponseEntity<List<InscripcionRespuestaDTO>> listarMisInscripciones(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                inscripcionService.listarMisInscripciones(jwt.getSubject()));
    }

    @GetMapping
    public ResponseEntity<List<InscripcionRespuestaDTO>> listarInscripciones() {
        return ResponseEntity.ok(inscripcionService.listarInscripciones());
    }

    @PatchMapping("/{idInscripcion}/estado")
    public ResponseEntity<InscripcionRespuestaDTO> actualizarEstado(
            @PathVariable Integer idInscripcion,
            @Valid @RequestBody EstadoInscripcionDTO datos) {
        return ResponseEntity.ok(inscripcionService.actualizarEstado(
                idInscripcion, datos.getEstadoInscripcion()));
    }
}
