package com.apirest.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // Permitir solicitudes desde nuestro frontend
            .cors(Customizer.withDefaults())

            // Desactivar CSRF porque utilizaremos JWT sin sesiones HTTP
            .csrf(csrf -> csrf.disable())

            // No almacenar sesiones en el servidor
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Definir los permisos de los endpoints
            .authorizeHttpRequests(auth -> auth

                    // Permitir solicitudes preflight del navegador
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Registro e inicio de sesión públicos
                    .requestMatchers(
                            "/api/auth/register",
                            "/api/auth/login"
                    ).permitAll()

                    // Health check público
                    .requestMatchers("/actuator/health").permitAll()

                    // Consultas de programas públicas
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/programas",
                            "/api/programas/**"
                    ).permitAll()

                    // Gestión de programas exclusiva del administrador
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/programas",
                            "/api/programas/**"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/programas",
                            "/api/programas/**"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/programas",
                            "/api/programas/**"
                    ).hasRole("ADMINISTRADOR")

                    // Administración de usuarios exclusiva del administrador
                    .requestMatchers("/api/usuarios/**")
                    .hasRole("ADMINISTRADOR")

                    // Los demás endpoints requieren autenticación
                    .anyRequest().authenticated()
            )

            // Activar autenticación mediante JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(
                                    jwtAuthenticationConverter()
                            )
                    )
            );

        return http.build();
    }

    // Convertir los roles del JWT en permisos de Spring Security
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return converter;
    }

    // Configuración global de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Accept")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}