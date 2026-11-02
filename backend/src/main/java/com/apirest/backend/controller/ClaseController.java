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
@RequestMapping("/api/clases")
@Tag(name = "Clases")
public class ClaseController {

    @Autowired
    IClaseService claseService;

    @GetMapping
    @Operation(summary = "Listar clases", description = "Consulta las clases disponibles. Es público.")
    @ApiResponse(responseCode = "200", description = "Lista de clases; puede estar vacía.")
    public ResponseEntity<List<ClaseRespuestaDTO>> listarClases() {
        return ResponseEntity.ok(claseService.listarClases());
    }

    @GetMapping("/{idClase}")
    @Operation(summary = "Obtener clase", description = "Busca una clase por su identificador. Es público.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clase encontrada."),
            @ApiResponse(responseCode = "404", description = "No existe una clase con ese ID.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<ClaseRespuestaDTO> obtenerClase(
            @Parameter(description = "Identificador de la clase.") @PathVariable Integer idClase) {
        return ResponseEntity.ok(claseService.obtenerClasePorId(idClase));
    }

    @PostMapping
    @Operation(summary = "Crear clase", description = "Asocia una clase a un profesor y un programa. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clase creada."),
            @ApiResponse(responseCode = "400", description = "Los datos no cumplen las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe el profesor o el programa indicado.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "La clase entra en conflicto con registros existentes.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<ClaseRespuestaDTO> crearClase(
            @Valid @RequestBody ClaseDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claseService.crearClase(datos));
    }

    @PutMapping("/{idClase}")
    @Operation(summary = "Actualizar clase", description = "Actualiza profesor, programa, fechas, nombre, cupo o estado. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clase actualizada."),
            @ApiResponse(responseCode = "400", description = "Los datos no cumplen las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe la clase, el profesor o el programa.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "El cambio entra en conflicto con registros existentes.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<ClaseRespuestaDTO> actualizarClase(
            @Parameter(description = "Identificador de la clase.") @PathVariable Integer idClase,
            @Valid @RequestBody ClaseDTO datos) {
        return ResponseEntity.ok(claseService.actualizarClase(idClase, datos));
    }

    @DeleteMapping("/{idClase}")
    @Operation(summary = "Eliminar clase", description = "Elimina una clase si no tiene registros asociados. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Clase eliminada."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe una clase con ese ID.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar por registros asociados.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Void> eliminarClase(@Parameter(description = "Identificador de la clase.") @PathVariable Integer idClase) {
        claseService.eliminarClase(idClase);
        return ResponseEntity.noContent().build();
    }
}
