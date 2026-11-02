package com.apirest.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearSesionClaseDTO {

    @NotNull(message = "El horario es obligatorio")
    @Positive(message = "El ID del horario debe ser mayor que cero")
    @Schema(description = "Identificador de un horario de la clase seleccionada.", example = "1")
    private Integer idHorario;

    @NotNull(message = "La fecha de la sesión es obligatoria")
    @Schema(description = "Fecha de sesión. Debe coincidir con el día semanal del horario y quedar dentro del periodo de la clase.", example = "2026-11-10", format = "date")
    private LocalDate fecha;
}
