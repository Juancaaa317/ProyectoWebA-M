package com.apirest.backend.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.apirest.backend.config.JwtConfig;
import com.apirest.backend.config.SecurityConfig;
import com.apirest.backend.service.IArchivoService;
import com.apirest.backend.service.IAsistenciaService;
import com.apirest.backend.service.IClaseService;
import com.apirest.backend.service.IHorarioService;
import com.apirest.backend.service.IInscripcionService;
import com.apirest.backend.service.IPagoService;
import com.apirest.backend.service.IProgramaService;
import com.apirest.backend.service.ISesionClaseService;
import com.apirest.backend.service.IUsuarioService;

/**
 * Verifica los permisos por rol de SecurityConfig desde el límite HTTP.
 * Carga todos los controladores con la cadena de seguridad real y firma tokens JWT
 * con la misma configuración que la aplicación; los servicios se simulan, así que no usa la BD.
 */
@WebMvcTest(properties = "app.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
@Import({ SecurityConfig.class, JwtConfig.class, SeguridadErrorHandler.class })
class SeguridadRutasTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtEncoder jwtEncoder;

    @MockitoBean IArchivoService archivoService;
    @MockitoBean IAsistenciaService asistenciaService;
    @MockitoBean IClaseService claseService;
    @MockitoBean IHorarioService horarioService;
    @MockitoBean IInscripcionService inscripcionService;
    @MockitoBean IPagoService pagoService;
    @MockitoBean IProgramaService programaService;
    @MockitoBean ISesionClaseService sesionService;
    @MockitoBean IUsuarioService usuarioService;

    // Mismos claims que genera JwtService al iniciar sesión
    private String token(String correo, String rol) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("arte-y-movimiento-api")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(5, ChronoUnit.MINUTES))
                .subject(correo)
                .claim("rol", rol)
                .claim("roles", List.of(rol))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return "Bearer " + jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // ---------- GET /api/clases/mias ----------

    @Test
    void misClasesExigeIniciarSesion() throws Exception {
        mockMvc.perform(get("/api/clases/mias"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(claseService, never()).listarMisClases(anyString());
    }

    @Test
    void misClasesEsSoloParaProfesores() throws Exception {
        for (String rol : List.of("ADMINISTRADOR", "ESTUDIANTE")) {
            mockMvc.perform(get("/api/clases/mias")
                            .header(HttpHeaders.AUTHORIZATION, token("otro@correo.com", rol)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value(403));
        }

        verify(claseService, never()).listarMisClases(anyString());
    }

    @Test
    void elProfesorRecibeSoloSusClases() throws Exception {
        when(claseService.listarMisClases("profe@correo.com")).thenReturn(List.of());

        mockMvc.perform(get("/api/clases/mias")
                        .header(HttpHeaders.AUTHORIZATION, token("profe@correo.com", "PROFESOR")))
                .andExpect(status().isOk());

        // El correo sale del token, nunca de un parámetro que el cliente pueda cambiar
        verify(claseService).listarMisClases("profe@correo.com");
    }

    @Test
    void elListadoGeneralDeClasesSigueSiendoPublico() throws Exception {
        when(claseService.listarClases()).thenReturn(List.of());

        mockMvc.perform(get("/api/clases"))
                .andExpect(status().isOk());
    }

    @Test
    void unTokenConFirmaFalsaSeRechaza() throws Exception {
        String valido = token("profe@correo.com", "PROFESOR");
        String alterado = valido.substring(0, valido.length() - 4) + "AAAA";

        mockMvc.perform(get("/api/clases/mias").header(HttpHeaders.AUTHORIZATION, alterado))
                .andExpect(status().isUnauthorized());
    }
}
