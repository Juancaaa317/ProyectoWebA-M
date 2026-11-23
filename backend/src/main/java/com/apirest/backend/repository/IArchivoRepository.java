package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.Archivo;

public interface IArchivoRepository extends JpaRepository<Archivo, Integer> {
}
