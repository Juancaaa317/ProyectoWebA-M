package com.apirest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstudianteListadoDTO {

    private Integer idEstudiante;
    private String nombre;
    private String tarjetaIdentidad;
    private String correo;
    private String telefono;
}
