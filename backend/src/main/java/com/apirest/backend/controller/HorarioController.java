package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.CrearHorarioDTO;
import com.apirest.backend.dto.HorarioRespuestaDTO;
import com.apirest.backend.service.IHorarioService;
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
@RequestMapping("/api/horarios")
@Tag(name = "Horarios")
public class HorarioController {

    @Autowired
    IHorarioService horarioService;

    @GetMapping
    @Operation(summary = "Listar horarios", description = "Consulta todos los horarios semanales. Es público.")
    @ApiResponse(responseCode = "200", description = "Lista de horarios; puede estar vacía.")
    public ResponseEntity<List<HorarioRespuestaDTO>> listarHorarios() {
        return ResponseEntity.ok(horarioService.listarHorarios());
    }

    @GetMapping("/clase/{idClase}")
    @Operation(summary = "Listar horarios de una clase", description = "Consulta los horarios asociados a una clase. Es público.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de horarios."),
            @ApiResponse(responseCode = "404", description = "No existe la clase indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<HorarioRespuestaDTO>> listarPorClase(
            @Parameter(description = "Identificador de la clase.") @PathVariable Integer idClase) {
        return ResponseEntity.ok(horarioService.listarPorClase(idClase));
    }

    @PostMapping
    @Operation(summary = "Crear horario", description = "Asigna un día y rango horario a una clase. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Horario creado."),
            @ApiResponse(responseCode = "400", description = "Los datos no son válidos o el rango horario es incorrecto.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe la clase indicada.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "El horario se cruza con otro horario existente.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<HorarioRespuestaDTO> crearHorario(
            @Valid @RequestBody CrearHorarioDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horarioService.crearHorario(datos));
    }

    @PutMapping("/{idHorario}")
    @Operation(summary = "Actualizar horario", description = "Reemplaza la clase, el día y el rango horario. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horario actualizado."),
            @ApiResponse(responseCode = "400", description = "Los datos no son válidos.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe el horario o la clase indicados.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "El horario entra en conflicto con otro.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<HorarioRespuestaDTO> actualizarHorario(
            @Parameter(description = "Identificador del horario.") @PathVariable Integer idHorario,
            @Valid @RequestBody CrearHorarioDTO datos) {
        return ResponseEntity.ok(horarioService.actualizarHorario(idHorario, datos));
    }

    @DeleteMapping("/{idHorario}")
    @Operation(summary = "Eliminar horario", description = "Elimina un horario que no tenga sesiones asociadas. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Horario eliminado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe el horario indicado.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar por sesiones asociadas.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Void> eliminarHorario(@Parameter(description = "Identificador del horario.") @PathVariable Integer idHorario) {
        horarioService.eliminarHorario(idHorario);
        return ResponseEntity.noContent().build();
    }
}
