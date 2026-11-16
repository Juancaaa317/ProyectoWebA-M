package com.apirest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRespuestaDTO {

    private String token;
    private UsuarioSesionDTO usuario;
}