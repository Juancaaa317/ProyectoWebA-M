package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.apirest.backend.dto.ApiErrorDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones")
public class InscripcionController {

    @Autowired
    IInscripcionService inscripcionService;

    @PostMapping
    @Operation(summary = "Solicitar inscripción", description = "El estudiante autenticado solicita cupo en una clase; comienza en estado PENDIENTE. Requiere rol ESTUDIANTE.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud creada."),
            @ApiResponse(responseCode = "400", description = "La clase no admite inscripciones en su estado actual.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ESTUDIANTE."),
            @ApiResponse(responseCode = "404", description = "No se encontró el perfil o la clase.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe una inscripción vigente o la clase está llena.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<InscripcionRespuestaDTO> crearInscripcion(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CrearInscripcionDTO datos) {

        InscripcionRespuestaDTO respuesta =
                inscripcionService.crearInscripcion(jwt.getSubject(), datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/mias")
    @Operation(summary = "Listar mis inscripciones", description = "Devuelve las inscripciones del estudiante autenticado. Requiere rol ESTUDIANTE.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones propias."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ESTUDIANTE."),
            @ApiResponse(responseCode = "404", description = "No se encontró el perfil de estudiante.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<InscripcionRespuestaDTO>> listarMisInscripciones(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                inscripcionService.listarMisInscripciones(jwt.getSubject()));
    }

    @GetMapping
    @Operation(summary = "Listar todas las inscripciones", description = "Consulta todas las inscripciones. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR.")
    })
    public ResponseEntity<List<InscripcionRespuestaDTO>> listarInscripciones() {
        return ResponseEntity.ok(inscripcionService.listarInscripciones());
    }

    @PatchMapping("/{idInscripcion}/estado")
    @Operation(summary = "Cambiar estado de inscripción", description = "Permite aprobar o cancelar una solicitud. Una inscripción cancelada puede reabrirse como pendiente, si hay cupo disponible. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado."),
            @ApiResponse(responseCode = "400", description = "La transición de estado no está permitida.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe la inscripción indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "La clase alcanzó su cupo máximo y no se puede reabrir la solicitud.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<InscripcionRespuestaDTO> actualizarEstado(
            @Parameter(description = "Identificador de la inscripción.") @PathVariable Integer idInscripcion,
            @Valid @RequestBody EstadoInscripcionDTO datos) {
        return ResponseEntity.ok(inscripcionService.actualizarEstado(
                idInscripcion, datos.getEstadoInscripcion()));
    }
}
