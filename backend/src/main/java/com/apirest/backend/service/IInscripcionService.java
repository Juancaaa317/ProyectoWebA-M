package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.Enums.EstadoInscripcion;
import com.apirest.backend.dto.CrearInscripcionDTO;
import com.apirest.backend.dto.InscripcionRespuestaDTO;

public interface IInscripcionService {

    InscripcionRespuestaDTO crearInscripcion(String correo, CrearInscripcionDTO datos);
    List<InscripcionRespuestaDTO> listarMisInscripciones(String correo);
    List<InscripcionRespuestaDTO> listarInscripciones();
    InscripcionRespuestaDTO actualizarEstado(Integer idInscripcion, EstadoInscripcion estado);
}
