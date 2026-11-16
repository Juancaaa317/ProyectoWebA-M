package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoSesion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EstadoSesionDTO {

    @NotNull(message = "El estado de la sesión es obligatorio")
    private EstadoSesion estadoSesion;
}
