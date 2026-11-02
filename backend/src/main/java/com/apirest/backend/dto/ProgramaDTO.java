package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoPrograma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProgramaDTO {

    @NotBlank(message = "El nombre del programa es obligatorio")
    @Size(max = 100, message = "El nombre del programa no puede superar los 100 caracteres")
    private String nombrePrograma;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;
    private EstadoPrograma estadoPrograma;
}
