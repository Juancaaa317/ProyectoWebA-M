package com.apirest.backend.dto;

import java.sql.Date;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroEstudianteDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 30, message = "La tarjeta de identidad no puede superar los 30 caracteres")
    private String tarjetaIdentidad;

    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private Date fechaNacimiento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;

    @NotBlank(message = "Debes confirmar la contraseña")
    private String confirmarContrasena;
}