package com.apirest.backend.controller;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.RegistroProfesorDTO;
import com.apirest.backend.dto.RegistroProfesorRespuestaDTO;
import com.apirest.backend.dto.EstudianteListadoDTO;
import com.apirest.backend.dto.ProfesorListadoDTO;
import com.apirest.backend.service.IUsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public class UsuarioController {

    @Autowired
    IUsuarioService usuarioService;

    @PostMapping("/profesores")
    @Operation(summary = "Registrar profesor", description = "Crea la cuenta con rol PROFESOR y su perfil docente. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profesor registrado."),
            @ApiResponse(responseCode = "400", description = "Los datos no cumplen las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<RegistroProfesorRespuestaDTO> registrarProfesor(
            @Valid @RequestBody RegistroProfesorDTO registro) {

        RegistroProfesorRespuestaDTO respuesta =
                usuarioService.registrarProfesor(registro);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/estudiantes")
    @Operation(summary = "Listar estudiantes", description = "Lista estudiantes y permite filtrar por nombre, correo o tarjeta de identidad. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de estudiantes; puede estar vacía."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR.")
    })
    public ResponseEntity<List<EstudianteListadoDTO>> listarEstudiantes(
            @Parameter(description = "Texto para buscar por nombre, correo o tarjeta de identidad.")
            @RequestParam(required = false) String buscar) {
        return ResponseEntity.ok(usuarioService.listarEstudiantes(buscar));
    }

    @GetMapping("/profesores")
    @Operation(summary = "Listar profesores", description = "Lista profesores y permite filtrar por nombre o correo. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de profesores; puede estar vacía."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR.")
    })
    public ResponseEntity<List<ProfesorListadoDTO>> listarProfesores(
            @Parameter(description = "Texto para buscar por nombre o correo.")
            @RequestParam(required = false) String buscar) {
        return ResponseEntity.ok(usuarioService.listarProfesores(buscar));
    }
}
