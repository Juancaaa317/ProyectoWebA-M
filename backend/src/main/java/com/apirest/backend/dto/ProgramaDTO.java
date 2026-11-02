package com.apirest.backend.dto;

import com.apirest.backend.Enums.EstadoPrograma;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProgramaDTO {

    @NotBlank(message = "El nombre del programa es obligatorio")
    private String nombrePrograma;

    private String descripcion;
    private EstadoPrograma estadoPrograma;
}
