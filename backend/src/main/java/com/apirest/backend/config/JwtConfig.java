package com.apirest.backend.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Obtener la clave secreta desde el archivo .env
    @Bean
    public SecretKey jwtSecretKey() {

        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(jwtSecret.trim());

        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "La clave JWT debe tener un formato Base64 válido."
            );
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "La clave JWT debe contener al menos 256 bits."
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // Componente encargado de generar los tokens
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {

        return NimbusJwtEncoder
                .withSecretKey(jwtSecretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    // Componente encargado de validar los tokens
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {

        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}