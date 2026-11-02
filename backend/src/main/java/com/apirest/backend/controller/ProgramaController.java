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

import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.dto.ProgramaDTO;
import com.apirest.backend.model.Programa;
import com.apirest.backend.service.IProgramaService;
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
@RequestMapping("/api/programas")
@Tag(name = "Programas")
public class ProgramaController {

    @Autowired
    IProgramaService programaService;

    // Crear un programa
    @PostMapping
    @Operation(summary = "Crear programa", description = "Registra un programa. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Programa creado."),
            @ApiResponse(responseCode = "400", description = "Los datos no cumplen las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "409", description = "Ya existe un programa con ese nombre.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Programa> crearPrograma(
            @Valid @RequestBody ProgramaDTO datos) {

        Programa programa = new Programa();
        programa.setNombrePrograma(datos.getNombrePrograma().trim());
        programa.setDescripcion(datos.getDescripcion());
        programa.setEstadoPrograma(datos.getEstadoPrograma());

        Programa nuevoPrograma = programaService.guardarPrograma(programa);

        return new ResponseEntity<>(
                nuevoPrograma, HttpStatus.CREATED);
    }

    // Listar todos los programas
    @GetMapping
    @Operation(summary = "Listar programas", description = "Consulta todos los programas. Es público.")
    @ApiResponse(responseCode = "200", description = "Lista de programas; puede estar vacía.")
    public ResponseEntity<List<Programa>> listarProgramas() {

        List<Programa> programas = programaService.listarProgramas();

        return ResponseEntity.ok(programas);
    }

    // Filtrar programas por estado
    // Filtrar por estado
    @GetMapping("/estado/{estadoPrograma}")
    @Operation(summary = "Filtrar programas por estado", description = "Estados permitidos: ACTIVO e INACTIVO. Es público.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de programas con ese estado."),
            @ApiResponse(responseCode = "400", description = "El estado no es válido.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<Programa>> filtrarPorEstado(
            @Parameter(description = "Estado del programa: ACTIVO o INACTIVO.")
            @PathVariable EstadoPrograma estadoPrograma) {

        List<Programa> programas = programaService.filtrarPorEstado(estadoPrograma);

        return ResponseEntity.ok(programas);
    }

    // Consultar un programa por su ID
    @GetMapping("/{idPrograma}")
    @Operation(summary = "Obtener programa", description = "Busca un programa por su identificador. Es público.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Programa encontrado."),
            @ApiResponse(responseCode = "404", description = "No existe un programa con ese ID.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Programa> obtenerProgramaPorId(
            @Parameter(description = "Identificador del programa.") @PathVariable Integer idPrograma) {

        return ResponseEntity.ok(programaService.obtenerProgramaPorId(idPrograma));
    }

    // Actualizar un programa
    @PutMapping("/{idPrograma}")
    @Operation(summary = "Actualizar programa", description = "Reemplaza los datos editables del programa. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Programa actualizado."),
            @ApiResponse(responseCode = "400", description = "Los datos no cumplen las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe un programa con ese ID.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "La actualización entra en conflicto con otro programa.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Programa> actualizarPrograma(
            @Parameter(description = "Identificador del programa.") @PathVariable Integer idPrograma,
            @Valid @RequestBody ProgramaDTO datos) {

        Programa cambios = new Programa();
        cambios.setNombrePrograma(datos.getNombrePrograma().trim());
        cambios.setDescripcion(datos.getDescripcion());
        cambios.setEstadoPrograma(datos.getEstadoPrograma());

        Programa programaActualizado =
                programaService.actualizarPrograma(idPrograma, cambios);

        return ResponseEntity.ok(programaActualizado);
    }

    // Eliminar un programa
    @DeleteMapping("/{idPrograma}")
    @Operation(summary = "Eliminar programa", description = "Elimina el programa si no tiene registros asociados. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Programa eliminado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe un programa con ese ID.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar porque tiene registros asociados.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Void> eliminarPrograma(
            @Parameter(description = "Identificador del programa.") @PathVariable Integer idPrograma) {

        programaService.eliminarPrograma(idPrograma);
        return ResponseEntity.noContent().build();
    }
}
