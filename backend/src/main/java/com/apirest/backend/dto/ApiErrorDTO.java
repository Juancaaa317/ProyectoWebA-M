package com.apirest.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ApiError", description = "Formato común de error generado por la API.")
public class ApiErrorDTO {

    @Schema(description = "Fecha y hora en que se produjo el error.", example = "2026-11-02T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP.", example = "400")
    private int status;

    @Schema(description = "Nombre del estado HTTP.", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje que explica el problema.", example = "nombre: El nombre es obligatorio")
    private String message;

    @Schema(description = "Ruta que recibió la solicitud.", example = "/api/programas")
    private String path;
}
