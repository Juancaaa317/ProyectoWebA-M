package com.apirest.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos que el usuario puede cambiar de su propio perfil. El correo, el rol y los datos de identidad no se editan aquí
@Data
@NoArgsConstructor
public class ActualizarPerfilDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    // Opcional: si se omite, el teléfono actual no cambia (un profesor puede estar registrado sin teléfono)
    @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos")
    private String telefono;
}
