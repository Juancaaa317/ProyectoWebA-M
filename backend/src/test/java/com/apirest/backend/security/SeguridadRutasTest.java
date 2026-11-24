package com.apirest.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Verifica los permisos por rol de SecurityConfig desde el límite HTTP, con tokens JWT reales.
 * La matriz de abajo es la especificación de quién puede llamar a cada endpoint: si alguien cambia
 * una regla de SecurityConfig (o el orden de las reglas), esta prueba lo detecta.
 */
class SeguridadRutasTest extends PruebaWebSegura {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String PROFESOR = "PROFESOR";
    private static final String ESTUDIANTE = "ESTUDIANTE";
    private static final List<String> ROLES = List.of(ADMIN, PROFESOR, ESTUDIANTE);

    private static final Set<String> PUBLICO = Set.of("PUBLICO");
    private static final Set<String> CUALQUIER_ROL = Set.of(ADMIN, PROFESOR, ESTUDIANTE);

    record Ruta(HttpMethod metodo, String path, Set<String> permitidos) {
        boolean esPublica() {
            return permitidos == PUBLICO;
        }

        @Override
        public String toString() {
            return metodo + " " + path;
        }
    }

    private static Ruta ruta(HttpMethod metodo, String path, Set<String> permitidos) {
        return new Ruta(metodo, path, permitidos);
    }

    static Stream<Ruta> rutas() {
        return Stream.of(
                // Autenticación
                ruta(HttpMethod.POST, "/api/auth/register", PUBLICO),
                ruta(HttpMethod.POST, "/api/auth/login", PUBLICO),

                // Programas
                ruta(HttpMethod.GET, "/api/programas", PUBLICO),
                ruta(HttpMethod.GET, "/api/programas/1", PUBLICO),
                ruta(HttpMethod.GET, "/api/programas/estado/ACTIVO", PUBLICO),
                ruta(HttpMethod.POST, "/api/programas", Set.of(ADMIN)),
                ruta(HttpMethod.PUT, "/api/programas/1", Set.of(ADMIN)),
                ruta(HttpMethod.DELETE, "/api/programas/1", Set.of(ADMIN)),

                // Clases
                ruta(HttpMethod.GET, "/api/clases", PUBLICO),
                ruta(HttpMethod.GET, "/api/clases/1", PUBLICO),
                ruta(HttpMethod.GET, "/api/clases/mias", Set.of(PROFESOR)),
                ruta(HttpMethod.POST, "/api/clases", Set.of(ADMIN)),
                ruta(HttpMethod.PUT, "/api/clases/1", Set.of(ADMIN)),
                ruta(HttpMethod.DELETE, "/api/clases/1", Set.of(ADMIN)),

                // Profesores (vista pública, sin datos de contacto)
                ruta(HttpMethod.GET, "/api/profesores", PUBLICO),

                // Horarios
                ruta(HttpMethod.GET, "/api/horarios", PUBLICO),
                ruta(HttpMethod.GET, "/api/horarios/clase/1", PUBLICO),
                ruta(HttpMethod.POST, "/api/horarios", Set.of(ADMIN)),
                ruta(HttpMethod.PUT, "/api/horarios/1", Set.of(ADMIN)),
                ruta(HttpMethod.DELETE, "/api/horarios/1", Set.of(ADMIN)),

                // Sesiones y asistencia
                ruta(HttpMethod.GET, "/api/sesiones/clase/1", PUBLICO),
                ruta(HttpMethod.POST, "/api/sesiones", Set.of(ADMIN)),
                ruta(HttpMethod.PATCH, "/api/sesiones/1/estado", Set.of(ADMIN)),
                ruta(HttpMethod.GET, "/api/sesiones/1/asistencias", Set.of(PROFESOR, ADMIN)),
                ruta(HttpMethod.PUT, "/api/sesiones/1/asistencias/1", Set.of(PROFESOR)),
                ruta(HttpMethod.GET, "/api/asistencias/mias", Set.of(ESTUDIANTE)),

                // Inscripciones
                ruta(HttpMethod.POST, "/api/inscripciones", Set.of(ESTUDIANTE)),
                ruta(HttpMethod.GET, "/api/inscripciones/mias", Set.of(ESTUDIANTE)),
                ruta(HttpMethod.GET, "/api/inscripciones", Set.of(ADMIN)),
                ruta(HttpMethod.PATCH, "/api/inscripciones/1/estado", Set.of(ADMIN)),

                // Pagos y archivos
                ruta(HttpMethod.POST, "/api/pagos", Set.of(ESTUDIANTE)),
                ruta(HttpMethod.GET, "/api/pagos/mios", Set.of(ESTUDIANTE)),
                ruta(HttpMethod.GET, "/api/pagos", Set.of(ADMIN)),
                ruta(HttpMethod.PATCH, "/api/pagos/1/estado", Set.of(ADMIN)),
                ruta(HttpMethod.GET, "/api/archivos/1", CUALQUIER_ROL),

                // Usuarios y perfil
                ruta(HttpMethod.GET, "/api/usuarios/me", CUALQUIER_ROL),
                ruta(HttpMethod.PUT, "/api/usuarios/me", CUALQUIER_ROL),
                ruta(HttpMethod.PUT, "/api/usuarios/me/contrasena", CUALQUIER_ROL),
                ruta(HttpMethod.POST, "/api/usuarios/profesores", Set.of(ADMIN)),
                ruta(HttpMethod.GET, "/api/usuarios/estudiantes", Set.of(ADMIN)),
                ruta(HttpMethod.GET, "/api/usuarios/profesores", Set.of(ADMIN)));
    }

    // Los escritos llevan un JSON vacío: la seguridad se evalúa antes que el cuerpo, así que
    // a un rol sin permiso le llega 403 aunque el cuerpo no sea válido.
    private MockHttpServletRequestBuilder peticion(Ruta ruta) {
        MockHttpServletRequestBuilder req = request(ruta.metodo(), ruta.path());
        if (ruta.metodo() != HttpMethod.GET && ruta.metodo() != HttpMethod.DELETE) {
            req.contentType(MediaType.APPLICATION_JSON).content("{}");
        }
        return req;
    }

    private int estado(MockHttpServletRequestBuilder req) throws Exception {
        return mockMvc.perform(req).andReturn().getResponse().getStatus();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rutas")
    void cadaRutaRespetaSusPermisos(Ruta ruta) throws Exception {
        int sinSesion = estado(peticion(ruta));
        if (ruta.esPublica()) {
            assertTrue(sinSesion != 401 && sinSesion != 403,
                    ruta + " debería ser pública y respondió " + sinSesion);
        } else {
            assertEquals(401, sinSesion, ruta + " sin sesión");
        }

        for (String rol : ROLES) {
            int conRol = estado(peticion(ruta)
                    .header(HttpHeaders.AUTHORIZATION, token("usuario@correo.com", rol)));
            boolean permitido = ruta.esPublica() || ruta.permitidos().contains(rol);
            if (permitido) {
                assertTrue(conRol != 401 && conRol != 403,
                        ruta + " debería permitir a " + rol + " y respondió " + conRol);
            } else {
                assertEquals(403, conRol, ruta + " con rol " + rol);
            }
        }
    }

    // ---------- Respuestas de error y tokens ----------

    @Test
    void losErroresDeSeguridadSalenConElFormatoDeLaApi() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/usuarios/me"));

        mockMvc.perform(get("/api/pagos").header(HttpHeaders.AUTHORIZATION, token("ana@correo.com", ESTUDIANTE)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void unTokenConFirmaFalsaSeRechaza() throws Exception {
        String valido = token("profe@correo.com", PROFESOR);
        String alterado = valido.substring(0, valido.length() - 4) + "AAAA";

        mockMvc.perform(get("/api/clases/mias").header(HttpHeaders.AUTHORIZATION, alterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unTokenVencidoSeRechaza() throws Exception {
        Instant hace = Instant.now().minus(2, ChronoUnit.HOURS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(hace)
                .expiresAt(hace.plus(60, ChronoUnit.MINUTES))
                .subject("profe@correo.com")
                .claim("roles", List.of(PROFESOR))
                .build();
        String vencido = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        mockMvc.perform(get("/api/clases/mias").header(HttpHeaders.AUTHORIZATION, "Bearer " + vencido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unTokenSinRolesNoDaPermisos() throws Exception {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .subject("alguien@correo.com")
                .build();
        String sinRoles = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        mockMvc.perform(get("/api/pagos").header(HttpHeaders.AUTHORIZATION, "Bearer " + sinRoles))
                .andExpect(status().isForbidden());
    }

    // ---------- GET /api/clases/mias ----------

    @Test
    void elProfesorRecibeSoloSusClasesSegunSuToken() throws Exception {
        when(claseService.listarMisClases("profe@correo.com")).thenReturn(List.of());

        mockMvc.perform(get("/api/clases/mias")
                        .header(HttpHeaders.AUTHORIZATION, token("profe@correo.com", PROFESOR)))
                .andExpect(status().isOk());

        // El correo sale del token, nunca de un parámetro que el cliente pueda cambiar
        verify(claseService).listarMisClases("profe@correo.com");
    }

    @Test
    void sinPermisoElServicioNiSiquieraSeLlama() throws Exception {
        mockMvc.perform(get("/api/clases/mias")
                        .header(HttpHeaders.AUTHORIZATION, token("admin@correo.com", ADMIN)))
                .andExpect(status().isForbidden());

        verify(claseService, never()).listarMisClases(anyString());
    }
}
