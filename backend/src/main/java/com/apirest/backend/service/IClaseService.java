package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.dto.ClaseDTO;
import com.apirest.backend.dto.ClaseRespuestaDTO;

public interface IClaseService {

    List<ClaseRespuestaDTO> listarClases();
    ClaseRespuestaDTO obtenerClasePorId(Integer idClase);
    ClaseRespuestaDTO crearClase(ClaseDTO datos);
    ClaseRespuestaDTO actualizarClase(Integer idClase, ClaseDTO datos);
    void eliminarClase(Integer idClase);
}
