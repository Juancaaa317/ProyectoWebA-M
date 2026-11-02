package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.Clase;

public interface IClaseRepository extends JpaRepository<Clase, Integer> {
}
