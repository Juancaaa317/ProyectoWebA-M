package com.apirest.backend.dto;

import java.sql.Time;

import com.apirest.backend.Enums.DiaSemana;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HorarioRespuestaDTO {

    private Integer idHorario;
    private Integer idClase;
    private String nombreClase;
    private String nombrePrograma;
    private DiaSemana diaSemana;
    private Time horaInicio;
    private Time horaFin;
}
