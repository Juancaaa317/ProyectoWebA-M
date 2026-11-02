package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.model.Programa;

public interface  IProgramaService {
    
    List<Programa> listarProgramas();
    List<Programa> filtrarPorEstado(EstadoPrograma estadoPrograma);
    Programa obtenerProgramaPorId(Integer idPrograma);
    Programa guardarPrograma(Programa programa);
    Programa actualizarPrograma( Integer idPrograma, Programa programa);
    String eliminarPrograma(Integer idPrograma);
}
