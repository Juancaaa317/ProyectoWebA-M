package com.apirest.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.LoginDTO;
import com.apirest.backend.dto.LoginRespuestaDTO;
import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;
import com.apirest.backend.dto.UsuarioSesionDTO;
import com.apirest.backend.exception.CredencialesInvalidasException;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.security.PruebaWebSegura;

// Registro e inicio de sesión desde HTTP: rutas públicas, validación del payload y códigos de error.
class AuthControllerTest extends PruebaWebSegura {

    private static final String REGISTRO_VALIDO = """
            {"nombre": "Ana Pérez", "correo": "ana@correo.com", "telefono": "3001234567",
             "contrasena": "ClaveSegura1", "confirmarContrasena": "ClaveSegura1"}
            """;

    private org.springframework.test.web.servlet.ResultActions registrar(String json) throws Exception {
        return mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private org.springframework.test.web.servlet.ResultActions login(String json) throws Exception {
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // ---------- POST /api/auth/register ----------

    @Test
    void registraUnEstudianteSinNecesitarSesion() throws Exception {
        when(usuarioService.registrarEstudiante(any())).thenReturn(
                new RegistroEstudianteRespuestaDTO(7, 3, "Ana Pérez", "ana@correo.com", Rol.ESTUDIANTE));

        registrar(REGISTRO_VALIDO)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("ana@correo.com"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                // La respuesta nunca devuelve la contraseña
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        ArgumentCaptor<RegistroEstudianteDTO> captor = ArgumentCaptor.forClass(RegistroEstudianteDTO.class);
        verify(usuarioService).registrarEstudiante(captor.capture());
        assertEquals("ana@correo.com", captor.getValue().getCorreo());
    }

    @ParameterizedTest(name = "rechaza: {0}")
    @ValueSource(strings = {
            // sin nombre
            """
            {"correo": "ana@correo.com", "telefono": "3001234567", "contrasena": "ClaveSegura1", "confirmarContrasena": "ClaveSegura1"}""",
            // correo con formato inválido
            """
            {"nombre": "Ana", "correo": "no-es-correo", "telefono": "3001234567", "contrasena": "ClaveSegura1", "confirmarContrasena": "ClaveSegura1"}""",
            // teléfono con letras
            """
            {"nombre": "Ana", "correo": "ana@correo.com", "telefono": "300ABC", "contrasena": "ClaveSegura1", "confirmarContrasena": "ClaveSegura1"}""",
            // contraseña de menos de 8 caracteres
            """
            {"nombre": "Ana", "correo": "ana@correo.com", "telefono": "3001234567", "contrasena": "corta", "confirmarContrasena": "corta"}""",
            // sin confirmar la contraseña
            """
            {"nombre": "Ana", "correo": "ana@correo.com", "telefono": "3001234567", "contrasena": "ClaveSegura1"}""",
            // fecha de nacimiento en el futuro
            """
            {"nombre": "Ana", "correo": "ana@correo.com", "telefono": "3001234567", "contrasena": "ClaveSegura1", "confirmarContrasena": "ClaveSegura1", "fechaNacimiento": "2999-01-01"}"""
    })
    void rechazaRegistrosInvalidosSinLlegarAlServicio(String json) throws Exception {
        registrar(json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void rechazaUnJsonMalFormado() throws Exception {
        registrar("{ esto no es json")
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void unCorreoYaRegistradoDevuelve409() throws Exception {
        when(usuarioService.registrarEstudiante(any()))
                .thenThrow(new RecursoDuplicadoException("Ya existe un usuario registrado con ese correo."));

        registrar(REGISTRO_VALIDO)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe un usuario registrado con ese correo."));
    }

    // ---------- POST /api/auth/login ----------

    @Test
    void iniciaSesionYDevuelveElToken() throws Exception {
        when(usuarioService.iniciarSesion(any())).thenReturn(new LoginRespuestaDTO("jwt-de-prueba",
                new UsuarioSesionDTO(7, "Ana Pérez", "3001234567", "ana@correo.com", Rol.ESTUDIANTE)));

        login("""
                {"correo": "ana@correo.com", "password": "ClaveSegura1"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-de-prueba"))
                .andExpect(jsonPath("$.usuario.rol").value("ESTUDIANTE"));

        ArgumentCaptor<LoginDTO> captor = ArgumentCaptor.forClass(LoginDTO.class);
        verify(usuarioService).iniciarSesion(captor.capture());
        assertEquals("ana@correo.com", captor.getValue().getCorreo());
    }

    @Test
    void credencialesIncorrectasDevuelven401ConMensajeGenerico() throws Exception {
        when(usuarioService.iniciarSesion(any())).thenThrow(new CredencialesInvalidasException());

        login("""
                {"correo": "ana@correo.com", "password": "otra"}""")
                .andExpect(status().isUnauthorized())
                // El mismo mensaje para correo inexistente o contraseña errada: no revela qué correos existen
                .andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos."));
    }

    @ParameterizedTest(name = "rechaza: {0}")
    @ValueSource(strings = {
            "{\"password\": \"ClaveSegura1\"}",
            "{\"correo\": \"ana@correo.com\"}",
            "{\"correo\": \"no-es-correo\", \"password\": \"ClaveSegura1\"}"
    })
    void rechazaLoginsIncompletos(String json) throws Exception {
        login(json).andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }
}
