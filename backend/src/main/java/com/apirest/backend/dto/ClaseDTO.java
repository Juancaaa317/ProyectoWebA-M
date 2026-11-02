package com.apirest.backend.dto;

import java.sql.Date;

import com.apirest.backend.Enums.EstadoClase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClaseDTO {

    @NotNull(message = "El profesor es obligatorio")
    @Positive(message = "El ID del profesor debe ser mayor que cero")
    private Integer idProfesor;

    @NotNull(message = "El programa es obligatorio")
    @Positive(message = "El ID del programa debe ser mayor que cero")
    private Integer idPrograma;

    private Date fechaInicio;
    private Date fechaFinalizacion;

    @NotBlank(message = "El nombre de la clase es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser mayor que cero")
    private Integer cupoMaximo;

    private EstadoClase estadoClase;
}
