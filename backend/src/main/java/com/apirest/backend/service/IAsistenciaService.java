package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.dto.AsistenciaEstudianteDTO;
import com.apirest.backend.dto.AsistenciaSesionDTO;
import com.apirest.backend.dto.MarcarAsistenciaDTO;

public interface IAsistenciaService {

    List<AsistenciaSesionDTO> listarPorSesion(
            Integer idSesion, String correo, boolean esAdministrador);

    AsistenciaSesionDTO marcarAsistencia(
            Integer idSesion, Integer idInscripcion,
            String correoProfesor, MarcarAsistenciaDTO datos);

    List<AsistenciaEstudianteDTO> listarMisAsistencias(String correo);
}
