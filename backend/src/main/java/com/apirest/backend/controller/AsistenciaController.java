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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.apirest.backend.dto.ApiErrorDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
@Tag(name = "Asistencias")
public class AsistenciaController {

    @Autowired
    IAsistenciaService asistenciaService;

    @GetMapping("/sesiones/{idSesion}/asistencias")
    @Operation(summary = "Consultar asistencia de una sesión", description = "Accesible para ADMINISTRADOR y el PROFESOR asignado a la clase.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de estudiantes activos y su estado de asistencia."),
            @ApiResponse(responseCode = "401", description = "Falta un token válido."),
            @ApiResponse(responseCode = "403", description = "El profesor no está asignado a la clase."),
            @ApiResponse(responseCode = "404", description = "No existe la sesión indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<AsistenciaSesionDTO>> listarPorSesion(
            @Parameter(description = "Identificador de la sesión.") @PathVariable Integer idSesion,
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("roles");
        boolean esAdministrador = roles != null && roles.contains("ADMINISTRADOR");

        return ResponseEntity.ok(asistenciaService.listarPorSesion(
                idSesion, correo, esAdministrador));
    }

    @PutMapping("/sesiones/{idSesion}/asistencias/{idInscripcion}")
    @Operation(summary = "Registrar asistencia", description = "Crea o actualiza presente, ausente o justificado para una inscripción activa. Solo puede hacerlo el profesor asignado.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asistencia guardada."),
            @ApiResponse(responseCode = "400", description = "La sesión o inscripción no permite registrar la asistencia.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Falta un token válido."),
            @ApiResponse(responseCode = "403", description = "Solo el profesor asignado puede registrar asistencia."),
            @ApiResponse(responseCode = "404", description = "No existe la sesión o inscripción indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<AsistenciaSesionDTO> marcarAsistencia(
            @Parameter(description = "Identificador de la sesión.") @PathVariable Integer idSesion,
            @Parameter(description = "Identificador de la inscripción del estudiante.") @PathVariable Integer idInscripcion,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MarcarAsistenciaDTO datos) {

        return ResponseEntity.ok(asistenciaService.marcarAsistencia(
                idSesion, idInscripcion, jwt.getSubject(), datos));
    }

    @GetMapping("/asistencias/mias")
    @Operation(summary = "Listar mi asistencia", description = "Devuelve el historial del estudiante autenticado. Requiere rol ESTUDIANTE.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial de asistencia propio."),
            @ApiResponse(responseCode = "401", description = "Falta un token válido."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ESTUDIANTE."),
            @ApiResponse(responseCode = "404", description = "No se encontró el perfil de estudiante.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<AsistenciaEstudianteDTO>> listarMisAsistencias(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(asistenciaService.listarMisAsistencias(
                jwt.getSubject()));
    }
}
