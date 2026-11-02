package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.Estudiante;

public interface IEstudianteRepository extends JpaRepository<Estudiante, Integer> {

        boolean existsByTarjetaIdentidad(String tarjetaIdentidad);
    
}
