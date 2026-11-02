package com.apirest.backend.dto;

import com.apirest.backend.Enums.AreaDocencia;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfesorListadoDTO {

    private Integer idProfesor;
    private String nombre;
    private String correo;
    private String telefono;
    private AreaDocencia areaDocencia;
}
