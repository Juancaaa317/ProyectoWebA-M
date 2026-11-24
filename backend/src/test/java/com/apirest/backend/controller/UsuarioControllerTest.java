package com.apirest.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.ActualizarPerfilDTO;
import com.apirest.backend.dto.CambiarContrasenaDTO;
import com.apirest.backend.dto.PerfilDTO;
import com.apirest.backend.security.PruebaWebSegura;

// Perfil propio (/me) y administración de usuarios desde HTTP, con tokens reales.
class UsuarioControllerTest extends PruebaWebSegura {

    private static final String ANA = "ana@correo.com";

    private PerfilDTO perfil(String nombre) {
        return new PerfilDTO(7, nombre, ANA, "3001234567", Rol.ESTUDIANTE, "1098765432", null, null);
    }

    // ---------- GET /api/usuarios/me ----------

    @Test
    void cadaUsuarioVeSuPropioPerfilSegunSuToken() throws Exception {
        when(usuarioService.obtenerPerfil(ANA)).thenReturn(perfil("Ana Pérez"));

        mockMvc.perform(get("/api/usuarios/me").header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(ANA))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"));

        verify(usuarioService).obtenerPerfil(ANA);
    }

    // ---------- PUT /api/usuarios/me ----------

    @Test
    void actualizaElPerfilPropio() throws Exception {
        when(usuarioService.actualizarPerfil(eq(ANA), any())).thenReturn(perfil("Ana María Pérez"));

        mockMvc.perform(put("/api/usuarios/me")
                        .header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Ana María Pérez\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana María Pérez"));

        ArgumentCaptor<ActualizarPerfilDTO> captor = ArgumentCaptor.forClass(ActualizarPerfilDTO.class);
        verify(usuarioService).actualizarPerfil(eq(ANA), captor.capture());
        assertEquals("Ana María Pérez", captor.getValue().getNombre());
        // Sin teléfono en el cuerpo, se envía null: el servicio conserva el actual
        assertNull(captor.getValue().getTelefono());
    }

    @Test
    void noSePuedeCambiarElCorreoNiElRolDesdeElPerfil() throws Exception {
        when(usuarioService.actualizarPerfil(eq(ANA), any())).thenReturn(perfil("Ana"));

        mockMvc.perform(put("/api/usuarios/me")
                        .header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Ana\", \"correo\": \"otro@correo.com\", \"rol\": \"ADMINISTRADOR\"}"))
                .andExpect(status().isOk());

        // Se ignoran: el perfil que se edita es siempre el del token
        verify(usuarioService).actualizarPerfil(eq(ANA), any());
    }

    @Test
    void rechazaUnPerfilSinNombreOConTelefonoInvalido() throws Exception {
        for (String json : List.of("{\"nombre\": \"  \"}", "{\"nombre\": \"Ana\", \"telefono\": \"12\"}")) {
            mockMvc.perform(put("/api/usuarios/me")
                            .header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(usuarioService);
    }

    // ---------- PUT /api/usuarios/me/contrasena ----------

    @Test
    void cambiaLaContrasenaYResponde204() throws Exception {
        mockMvc.perform(put("/api/usuarios/me/contrasena")
                        .header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contrasenaActual": "ClaveVieja1", "contrasenaNueva": "ClaveNueva1", "confirmarContrasena": "ClaveNueva1"}"""))
                .andExpect(status().isNoContent());

        verify(usuarioService).cambiarContrasena(eq(ANA), any(CambiarContrasenaDTO.class));
    }

    @Test
    void unaContrasenaActualErradaEsUn400YNoUn401() throws Exception {
        // Con un 401 el frontend cerraría la sesión: equivocarse al escribirla no debe desconectar al usuario
        doThrow(new IllegalArgumentException("La contraseña actual no es correcta."))
                .when(usuarioService).cambiarContrasena(eq(ANA), any());

        mockMvc.perform(put("/api/usuarios/me/contrasena")
                        .header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contrasenaActual": "Equivocada", "contrasenaNueva": "ClaveNueva1", "confirmarContrasena": "ClaveNueva1"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La contraseña actual no es correcta."));
    }

    @Test
    void rechazaUnaContrasenaNuevaCorta() throws Exception {
        mockMvc.perform(put("/api/usuarios/me/contrasena")
                        .header(HttpHeaders.AUTHORIZATION, token(ANA, "ESTUDIANTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contrasenaActual": "ClaveVieja1", "contrasenaNueva": "corta", "confirmarContrasena": "corta"}"""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    // ---------- Administración (solo ADMINISTRADOR) ----------

    @Test
    void elAdministradorRegistraUnProfesorConDatosValidos() throws Exception {
        mockMvc.perform(post("/api/usuarios/profesores")
                        .header(HttpHeaders.AUTHORIZATION, token("admin@correo.com", "ADMINISTRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Laura Gómez", "correo": "laura@correo.com", "contrasena": "ClaveSegura1",
                                 "confirmarContrasena": "ClaveSegura1", "areaDocencia": "DOCENTE"}"""))
                .andExpect(status().isCreated());

        verify(usuarioService).registrarProfesor(any());
    }

    @Test
    void rechazaUnAreaDeDocenciaQueNoExiste() throws Exception {
        mockMvc.perform(post("/api/usuarios/profesores")
                        .header(HttpHeaders.AUTHORIZATION, token("admin@correo.com", "ADMINISTRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Laura", "correo": "laura@correo.com", "contrasena": "ClaveSegura1",
                                 "confirmarContrasena": "ClaveSegura1", "areaDocencia": "INVENTADA"}"""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void laBusquedaDeEstudiantesPasaElFiltroAlServicio() throws Exception {
        when(usuarioService.listarEstudiantes("ana")).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios/estudiantes").param("buscar", "ana")
                        .header(HttpHeaders.AUTHORIZATION, token("admin@correo.com", "ADMINISTRADOR")))
                .andExpect(status().isOk());

        verify(usuarioService).listarEstudiantes("ana");
    }
}
