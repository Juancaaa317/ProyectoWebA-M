package com.apirest.backend.dto;

import java.sql.Date;
import java.sql.Time;

import com.apirest.backend.Enums.EstadoSesion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SesionClaseRespuestaDTO {

    private Integer idSesion;
    private Integer idClase;
    private String nombreClase;
    private String nombrePrograma;
    private Date fecha;
    private Time horaInicio;
    private Time horaFin;
    private EstadoSesion estadoSesion;
}
