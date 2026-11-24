package com.apirest.backend.dto;

import java.util.List;

import com.apirest.backend.Enums.AreaDocencia;

import lombok.AllArgsConstructor;
import lombok.Data;

// Datos de un profesor que se pueden mostrar en la página pública.
// A propósito NO incluye correo, teléfono, cédula, dirección ni fecha de nacimiento.
@Data
@AllArgsConstructor
public class ProfesorPublicoDTO {

    private Integer idProfesor;
    private String nombre;
    private AreaDocencia areaDocencia;

    // Programas activos que dicta en clases abiertas (en curso o programadas), sin repetir y en orden alfabético
    private List<String> disciplinas;
}
