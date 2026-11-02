package com.apirest.backend.service;

import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;

public interface IUsuarioService {

    RegistroEstudianteRespuestaDTO registrarEstudiante(RegistroEstudianteDTO registro);
}