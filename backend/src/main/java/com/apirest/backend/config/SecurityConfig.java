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

                    // Consultas de clases públicas
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/clases",
                            "/api/clases/**"
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

                    // Gestión de clases exclusiva del administrador
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/clases",
                            "/api/clases/**"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/clases",
                            "/api/clases/**"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/clases",
                            "/api/clases/**"
                    ).hasRole("ADMINISTRADOR")

                    // Inscripción y consulta de las clases propias del estudiante
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/inscripciones"
                    ).hasRole("ESTUDIANTE")

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/inscripciones/mias"
                    ).hasRole("ESTUDIANTE")

                    // Gestión de inscripciones exclusiva del administrador
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/inscripciones"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/inscripciones/*/estado"
                    ).hasRole("ADMINISTRADOR")

                    // Consulta pública de horarios semanales
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/horarios",
                            "/api/horarios/clase/**"
                    ).permitAll()

                    // Gestión de horarios exclusiva del administrador
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/horarios"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/horarios/**"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.DELETE,
                            "/api/horarios/**"
                    ).hasRole("ADMINISTRADOR")

                    // Consulta de sesiones por clase
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/sesiones/clase/**"
                    ).permitAll()

                    // Creación y cambio de estado de sesiones por el administrador
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/sesiones"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/sesiones/*/estado"
                    ).hasRole("ADMINISTRADOR")

                    // El profesor asignado toma asistencia; administrador puede consultarla
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/sesiones/*/asistencias"
                    ).hasAnyRole("PROFESOR", "ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/sesiones/*/asistencias/*"
                    ).hasRole("PROFESOR")

                    // Consulta de asistencia propia por el estudiante
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/asistencias/mias"
                    ).hasRole("ESTUDIANTE")

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
