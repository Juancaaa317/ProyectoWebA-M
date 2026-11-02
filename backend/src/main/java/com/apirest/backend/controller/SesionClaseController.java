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
@RequestMapping("/api/sesiones")
@Tag(name = "Sesiones")
public class SesionClaseController {

    @Autowired
    ISesionClaseService sesionService;

    @GetMapping("/clase/{idClase}")
    @Operation(summary = "Listar sesiones de una clase", description = "Consulta las sesiones ordenadas por fecha y hora. Es público.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de sesiones."),
            @ApiResponse(responseCode = "404", description = "No existe la clase indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<SesionClaseRespuestaDTO>> listarPorClase(
            @Parameter(description = "Identificador de la clase.") @PathVariable Integer idClase) {
        return ResponseEntity.ok(sesionService.listarPorClase(idClase));
    }

    @PostMapping
    @Operation(summary = "Programar sesión", description = "Crea una sesión a partir de un horario y una fecha que debe coincidir con el día de semana. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sesión programada."),
            @ApiResponse(responseCode = "400", description = "La fecha no corresponde al día del horario o no es válida.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe el horario indicado.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe una sesión para esa clase, fecha y hora.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<SesionClaseRespuestaDTO> crearSesion(
            @Valid @RequestBody CrearSesionClaseDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sesionService.crearSesion(datos));
    }

    @PatchMapping("/{idSesion}/estado")
    @Operation(summary = "Cambiar estado de sesión", description = "Estados: PROGRAMADA, REALIZADA o CANCELADA. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado."),
            @ApiResponse(responseCode = "400", description = "La transición de estado no está permitida.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe la sesión indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<SesionClaseRespuestaDTO> actualizarEstado(
            @Parameter(description = "Identificador de la sesión.") @PathVariable Integer idSesion,
            @Valid @RequestBody EstadoSesionDTO datos) {
        return ResponseEntity.ok(
                sesionService.actualizarEstado(idSesion, datos.getEstadoSesion()));
    }
}
