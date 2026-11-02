package com.apirest.backend.dto;

import java.sql.Date;
import java.sql.Time;

import com.apirest.backend.Enums.EstadoAsistencia;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsistenciaEstudianteDTO {

    private Integer idAsistencia;
    private Integer idClase;
    private String nombreClase;
    private String nombrePrograma;
    private Date fecha;
    private Time horaInicio;
    private Time horaFin;
    private EstadoAsistencia estado;
    private String observaciones;
}
