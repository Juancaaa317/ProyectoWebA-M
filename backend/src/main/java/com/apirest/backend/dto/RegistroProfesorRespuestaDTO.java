package com.apirest.backend.dto;

import com.apirest.backend.Enums.AreaDocencia;
import com.apirest.backend.Enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroProfesorRespuestaDTO {

    private Integer idUsuario;
    private Integer idProfesor;
    private String nombre;
    private String correo;
    private AreaDocencia areaDocencia;
    private Rol rol;
}
