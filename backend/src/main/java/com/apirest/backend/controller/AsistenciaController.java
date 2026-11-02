package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.AsistenciaEstudianteDTO;
import com.apirest.backend.dto.AsistenciaSesionDTO;
import com.apirest.backend.dto.MarcarAsistenciaDTO;
import com.apirest.backend.service.IAsistenciaService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class AsistenciaController {

    @Autowired
    IAsistenciaService asistenciaService;

    @GetMapping("/sesiones/{idSesion}/asistencias")
    public ResponseEntity<List<AsistenciaSesionDTO>> listarPorSesion(
            @PathVariable Integer idSesion,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("roles");
        boolean esAdministrador = roles != null && roles.contains("ADMINISTRADOR");

        return ResponseEntity.ok(asistenciaService.listarPorSesion(
                idSesion, correo, esAdministrador));
    }

    @PutMapping("/sesiones/{idSesion}/asistencias/{idInscripcion}")
    public ResponseEntity<AsistenciaSesionDTO> marcarAsistencia(
            @PathVariable Integer idSesion,
            @PathVariable Integer idInscripcion,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MarcarAsistenciaDTO datos) {

        return ResponseEntity.ok(asistenciaService.marcarAsistencia(
                idSesion, idInscripcion, jwt.getSubject(), datos));
    }

    @GetMapping("/asistencias/mias")
    public ResponseEntity<List<AsistenciaEstudianteDTO>> listarMisAsistencias(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(asistenciaService.listarMisAsistencias(
                jwt.getSubject()));
    }
}
