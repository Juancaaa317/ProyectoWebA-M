package com.apirest.backend.service;

import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;
import com.apirest.backend.dto.LoginDTO;
import com.apirest.backend.dto.LoginRespuestaDTO;
import com.apirest.backend.dto.RegistroProfesorDTO;
import com.apirest.backend.dto.RegistroProfesorRespuestaDTO;

public interface IUsuarioService {

    RegistroEstudianteRespuestaDTO registrarEstudiante(RegistroEstudianteDTO registro);

    LoginRespuestaDTO iniciarSesion(LoginDTO login);

    RegistroProfesorRespuestaDTO registrarProfesor(RegistroProfesorDTO registro);
}
