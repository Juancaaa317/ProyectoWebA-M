package com.apirest.backend.dto;

import java.sql.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearSesionClaseDTO {

    @NotNull(message = "El horario es obligatorio")
    @Positive(message = "El ID del horario debe ser mayor que cero")
    private Integer idHorario;

    @NotNull(message = "La fecha de la sesión es obligatoria")
    private Date fecha;
}
