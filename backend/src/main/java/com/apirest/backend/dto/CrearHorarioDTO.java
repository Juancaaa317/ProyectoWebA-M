package com.apirest.backend.dto;

import java.sql.Time;

import com.apirest.backend.Enums.DiaSemana;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearHorarioDTO {

    @NotNull(message = "La clase es obligatoria")
    @Positive(message = "El ID de la clase debe ser mayor que cero")
    private Integer idClase;

    @NotNull(message = "El día de la semana es obligatorio")
    private DiaSemana diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private Time horaInicio;

    @NotNull(message = "La hora de finalización es obligatoria")
    private Time horaFin;
}
