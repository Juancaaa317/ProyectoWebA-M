package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoAsistencia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarcarAsistenciaDTO {

    @NotNull(message = "El estado de asistencia es obligatorio")
    private EstadoAsistencia estado;

    @Size(max = 500, message = "La observación no puede superar los 500 caracteres")
    private String observaciones;
}
