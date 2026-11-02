package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoAsistencia;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsistenciaSesionDTO {

    private Integer idInscripcion;
    private Integer idEstudiante;
    private String nombreEstudiante;
    private EstadoAsistencia estado;
    private String observaciones;
}
