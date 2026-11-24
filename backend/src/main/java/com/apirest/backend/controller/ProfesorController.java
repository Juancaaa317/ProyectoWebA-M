package com.apirest.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.ProfesorPublicoDTO;
import com.apirest.backend.service.IProfesorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profesores")
@Tag(name = "Profesores")
@RequiredArgsConstructor
public class ProfesorController {

    private final IProfesorService profesorService;

    @GetMapping
    @Operation(summary = "Listar profesores (público)", description = "Equipo docente para la página pública: nombre, área y disciplinas que dicta en clases abiertas. No expone datos de contacto ni de identidad. Es público.")
    @ApiResponse(responseCode = "200", description = "Lista de profesores ordenada por nombre; puede estar vacía.")
    public ResponseEntity<List<ProfesorPublicoDTO>> listarProfesores() {
        return ResponseEntity.ok(profesorService.listarPublicos());
    }
}
