package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.CrearSesionClaseDTO;
import com.apirest.backend.dto.EstadoSesionDTO;
import com.apirest.backend.dto.SesionClaseRespuestaDTO;
import com.apirest.backend.service.ISesionClaseService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/sesiones")
public class SesionClaseController {

    @Autowired
    ISesionClaseService sesionService;

    @GetMapping("/clase/{idClase}")
    public ResponseEntity<List<SesionClaseRespuestaDTO>> listarPorClase(
            @PathVariable Integer idClase) {
        return ResponseEntity.ok(sesionService.listarPorClase(idClase));
    }

    @PostMapping
    public ResponseEntity<SesionClaseRespuestaDTO> crearSesion(
            @Valid @RequestBody CrearSesionClaseDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sesionService.crearSesion(datos));
    }

    @PatchMapping("/{idSesion}/estado")
    public ResponseEntity<SesionClaseRespuestaDTO> actualizarEstado(
            @PathVariable Integer idSesion,
            @Valid @RequestBody EstadoSesionDTO datos) {
        return ResponseEntity.ok(
                sesionService.actualizarEstado(idSesion, datos.getEstadoSesion()));
    }
}
