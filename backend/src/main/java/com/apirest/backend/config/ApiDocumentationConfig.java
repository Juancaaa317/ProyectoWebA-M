package com.apirest.backend.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Arte y Movimiento",
                version = "1.0.0",
                description = "API REST para autenticación y gestión académica "
                        + "de Arte y Movimiento. Las operaciones protegidas usan "
                        + "JWT Bearer y permisos por rol."),
        tags = {
                @Tag(name = "Autenticación", description = "Registro e inicio de sesión."),
                @Tag(name = "Usuarios", description = "Consulta de estudiantes y profesores; registro docente."),
                @Tag(name = "Programas", description = "Programas académicos de la academia."),
                @Tag(name = "Clases", description = "Clases, profesores y programas asociados."),
                @Tag(name = "Horarios", description = "Horarios semanales de las clases."),
                @Tag(name = "Sesiones", description = "Sesiones programadas para una clase."),
                @Tag(name = "Inscripciones", description = "Solicitudes y estados de inscripción."),
                @Tag(name = "Asistencias", description = "Consulta y registro de asistencia.")
        })
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Usa el token devuelto por POST /api/auth/login. "
                + "En Swagger UI pega solo el token; la interfaz agrega Bearer.")
public class ApiDocumentationConfig {
}
