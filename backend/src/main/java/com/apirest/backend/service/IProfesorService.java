package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.dto.ProfesorPublicoDTO;

public interface IProfesorService {

    List<ProfesorPublicoDTO> listarPublicos();
}
