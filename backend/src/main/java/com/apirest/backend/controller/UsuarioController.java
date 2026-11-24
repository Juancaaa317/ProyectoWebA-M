package com.apirest.backend.controller;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.ActualizarPerfilDTO;
import com.apirest.backend.dto.CambiarContrasenaDTO;
import com.apirest.backend.dto.PerfilDTO;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Ver mi perfil", description = "Devuelve los datos del usuario autenticado, más los propios de su rol (estudiante: tarjeta de identidad y fecha de nacimiento; profesor: área de docencia). Cualquier rol autenticado.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil del usuario."),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario o su perfil de rol.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<PerfilDTO> obtenerPerfil(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(jwt.getSubject()));
    }

    @PutMapping("/me")
    @Operation(summary = "Editar mi perfil", description = "Actualiza el nombre y, si se envía, el teléfono del usuario autenticado (también en su perfil de estudiante o profesor). Si se omite el teléfono, no cambia. El correo, el rol y los datos de identidad no se pueden editar aquí.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado."),
            @ApiResponse(responseCode = "400", description = "Los datos no cumplen las validaciones.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario o su perfil de rol.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<PerfilDTO> actualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ActualizarPerfilDTO datos) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(jwt.getSubject(), datos));
    }

    @PutMapping("/me/contrasena")
    @Operation(summary = "Cambiar mi contraseña", description = "Cambia la contraseña del usuario autenticado, comprobando la actual. Una contraseña actual incorrecta responde 400 (no 401) para no cerrar la sesión.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contraseña cambiada."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, las contraseñas no coinciden o la actual es incorrecta.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Void> cambiarContrasena(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CambiarContrasenaDTO datos) {
        usuarioService.cambiarContrasena(jwt.getSubject(), datos);
        return ResponseEntity.noContent().build();
    }

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
