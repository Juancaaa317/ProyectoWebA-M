package com.apirest.backend.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;
import com.apirest.backend.dto.LoginDTO;
import com.apirest.backend.dto.LoginRespuestaDTO;
import com.apirest.backend.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.apirest.backend.dto.ApiErrorDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
public class AuthController {

    @Autowired
    IUsuarioService usuarioService;

    // Registrar un nuevo estudiante
    @PostMapping("/register")
    @Operation(summary = "Registrar estudiante", description = "Crea una cuenta con rol ESTUDIANTE y su perfil académico. No requiere token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estudiante registrado."),
            @ApiResponse(responseCode = "400", description = "Los campos no cumplen las validaciones o las contraseñas no coinciden.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "El correo o la tarjeta de identidad ya están registrados.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    ResponseEntity<RegistroEstudianteRespuestaDTO> registrarEstudiante(@Valid @RequestBody RegistroEstudianteDTO registro) {
        RegistroEstudianteRespuestaDTO respuesta=usuarioService.registrarEstudiante(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y devuelve el JWT y los datos básicos de la sesión. No requiere token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales válidas; devuelve el token JWT."),
            @ApiResponse(responseCode = "400", description = "El cuerpo no cumple las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos.")
    })
    ResponseEntity<LoginRespuestaDTO> iniciarSesion(@Valid @RequestBody LoginDTO login) {
        return ResponseEntity.ok(usuarioService.iniciarSesion(login));
    }
}
