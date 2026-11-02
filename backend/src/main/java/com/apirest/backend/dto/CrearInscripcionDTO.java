package com.apirest.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearInscripcionDTO {

    @NotNull(message = "La clase es obligatoria")
    @Positive(message = "El ID de la clase debe ser mayor que cero")
    private Integer idClase;
}
