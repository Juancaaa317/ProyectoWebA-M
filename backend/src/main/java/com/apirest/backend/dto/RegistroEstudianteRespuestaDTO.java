package com.apirest.backend.dto;

import com.apirest.backend.Enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroEstudianteRespuestaDTO {

    private Integer idUsuario;

    private Integer idEstudiante;

    private String nombre;

    private String correo;

    private Rol rol;
}