package com.apirest.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.model.Programa;

public interface IProgramaRepository extends JpaRepository<Programa, Integer>{
    
    List<Programa> findByEstadoPrograma(EstadoPrograma estadoPrograma);

   
}
