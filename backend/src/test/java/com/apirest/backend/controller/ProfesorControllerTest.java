package com.apirest.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.apirest.backend.Enums.AreaDocencia;
import com.apirest.backend.dto.ProfesorPublicoDTO;
import com.apirest.backend.security.PruebaWebSegura;

// GET /api/profesores: público y sin datos de contacto ni de identidad.
class ProfesorControllerTest extends PruebaWebSegura {

    @Test
    void listaLosProfesoresSinIniciarSesion() throws Exception {
        when(profesorService.listarPublicos()).thenReturn(List.of(
                new ProfesorPublicoDTO(1, "Laura Gómez", AreaDocencia.PROFESIONAL, List.of("Ballet", "Danza árabe"))));

        mockMvc.perform(get("/api/profesores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laura Gómez"))
                .andExpect(jsonPath("$[0].areaDocencia").value("PROFESIONAL"))
                .andExpect(jsonPath("$[0].disciplinas[1]").value("Danza árabe"));
    }

    @Test
    void laRespuestaSoloTraeLosCamposPublicos() throws Exception {
        when(profesorService.listarPublicos()).thenReturn(List.of(
                new ProfesorPublicoDTO(1, "Laura Gómez", AreaDocencia.PROFESIONAL, List.of())));

        // Exactamente 4 campos: si alguien agrega correo, teléfono u otro dato al DTO, esta prueba lo detecta
        mockMvc.perform(get("/api/profesores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].length()").value(4))
                .andExpect(jsonPath("$[0].idProfesor").exists())
                .andExpect(jsonPath("$[0].nombre").exists())
                .andExpect(jsonPath("$[0].areaDocencia").exists())
                .andExpect(jsonPath("$[0].disciplinas").exists())
                .andExpect(jsonPath("$[0].correo").doesNotExist())
                .andExpect(jsonPath("$[0].telefono").doesNotExist())
                .andExpect(jsonPath("$[0].cedulaCiudadana").doesNotExist());
    }
}
