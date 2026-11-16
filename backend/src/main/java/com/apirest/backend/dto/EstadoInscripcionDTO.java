package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoInscripcion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EstadoInscripcionDTO {

    @NotNull(message = "El estado de la inscripción es obligatorio")
    private EstadoInscripcion estadoInscripcion;
}
