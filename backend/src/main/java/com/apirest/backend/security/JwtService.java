package com.apirest.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import com.apirest.backend.model.Usuario;

@Service
public class JwtService {

    @Autowired
    JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-minutes:60}")
    private long expirationMinutes;

    // Generar token JWT para el usuario autenticado
    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        // Construir la información que llevará el token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("arte-y-movimiento-api")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(
                        expirationMinutes,
                        ChronoUnit.MINUTES
                ))
                .subject(usuario.getCorreo())
                .claim("userId", usuario.getIdUsuario())
                .claim("rol", usuario.getRol().name())
                .claim("roles", List.of(usuario.getRol().name()))
                .build();

        // Definir el algoritmo de firma
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        // Generar y devolver el token
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}