package com.apirest.backend.dto;

import com.apirest.backend.Enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioSesionDTO {

    private Integer id;
    private String nombre;
    private String telefono;
    private String correo;
    private Rol rol;
}
