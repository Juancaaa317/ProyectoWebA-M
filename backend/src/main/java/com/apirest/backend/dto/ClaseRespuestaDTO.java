package com.apirest.backend.dto;

import java.sql.Date;

import com.apirest.backend.Enums.EstadoClase;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClaseRespuestaDTO {

    private Integer idClase;
    private Integer idProfesor;
    private String nombreProfesor;
    private Integer idPrograma;
    private String nombrePrograma;
    private Date fechaInicio;
    private Date fechaFinalizacion;
    private String nombre;
    private Integer cupoMaximo;
    private EstadoClase estadoClase;
}
