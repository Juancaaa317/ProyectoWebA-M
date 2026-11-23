package com.apirest.backend.dto;

import java.util.Date;

import com.apirest.backend.Enums.AreaDocencia;
import com.apirest.backend.Enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Data;

// Perfil del usuario autenticado. Los campos propios de un rol (tarjeta, fecha de nacimiento, área) vienen en null para los demás roles
@Data
@AllArgsConstructor
public class PerfilDTO {

    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private Rol rol;
    private String tarjetaIdentidad;
    private Date fechaNacimiento;
    private AreaDocencia areaDocencia;
}
