package com.apirest.backend.dto;

import java.sql.Date;

import com.apirest.backend.Enums.EstadoInscripcion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InscripcionRespuestaDTO {

    private Integer idInscripcion;
    private Integer idEstudiante;
    private String nombreEstudiante;
    private Integer idClase;
    private String nombreClase;
    private String nombrePrograma;
    private Date fechaInscripcion;
    private EstadoInscripcion estadoInscripcion;
}
