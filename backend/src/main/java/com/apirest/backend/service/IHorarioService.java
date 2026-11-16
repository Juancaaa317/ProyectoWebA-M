package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.dto.CrearHorarioDTO;
import com.apirest.backend.dto.HorarioRespuestaDTO;

public interface IHorarioService {

    List<HorarioRespuestaDTO> listarHorarios();
    List<HorarioRespuestaDTO> listarPorClase(Integer idClase);
    HorarioRespuestaDTO crearHorario(CrearHorarioDTO datos);
    HorarioRespuestaDTO actualizarHorario(Integer idHorario, CrearHorarioDTO datos);
    void eliminarHorario(Integer idHorario);
}
