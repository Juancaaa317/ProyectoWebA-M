package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.Enums.EstadoSesion;
import com.apirest.backend.dto.CrearSesionClaseDTO;
import com.apirest.backend.dto.SesionClaseRespuestaDTO;

public interface ISesionClaseService {

    List<SesionClaseRespuestaDTO> listarPorClase(Integer idClase);
    SesionClaseRespuestaDTO crearSesion(CrearSesionClaseDTO datos);
    SesionClaseRespuestaDTO actualizarEstado(Integer idSesion, EstadoSesion estado);
}
