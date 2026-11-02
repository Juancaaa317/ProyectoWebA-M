package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.Profesor;

public interface IProfesorRepository extends JpaRepository<Profesor, Integer> {
}
