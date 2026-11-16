package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoPago;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EstadoPagoDTO {

    @NotNull(message = "El estado del pago es obligatorio")
    private EstadoPago estado;

    @Size(max = 255, message = "La observación admite máximo 255 caracteres")
    private String observacion;
}
