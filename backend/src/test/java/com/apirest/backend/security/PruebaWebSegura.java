package com.apirest.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import com.apirest.backend.service.IProfesorService;
import com.apirest.backend.service.IProgramaService;
import com.apirest.backend.service.ISesionClaseService;
import com.apirest.backend.service.IUsuarioService;

/**
 * Base de las pruebas HTTP con la seguridad real: carga todos los controladores, SecurityConfig
 * y la configuración JWT de la aplicación. Los servicios se simulan, así que no necesita MySQL.
 * Todas las subclases comparten el mismo contexto de Spring (se levanta una sola vez).
 */
@WebMvcTest(properties = "app.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
@Import({ SecurityConfig.class, JwtConfig.class, SeguridadErrorHandler.class })
public abstract class PruebaWebSegura {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    JwtEncoder jwtEncoder;

    @MockitoBean protected IArchivoService archivoService;
    @MockitoBean protected IAsistenciaService asistenciaService;
    @MockitoBean protected IClaseService claseService;
    @MockitoBean protected IHorarioService horarioService;
    @MockitoBean protected IInscripcionService inscripcionService;
    @MockitoBean protected IPagoService pagoService;
    @MockitoBean protected IProfesorService profesorService;
    @MockitoBean protected IProgramaService programaService;
    @MockitoBean protected ISesionClaseService sesionService;
    @MockitoBean protected IUsuarioService usuarioService;

    /** Cabecera Authorization con un token firmado igual que los que genera JwtService al iniciar sesión. */
    protected String token(String correo, String rol) {
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
}
