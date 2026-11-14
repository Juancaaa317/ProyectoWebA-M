package com.apirest.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.Archivo;

public interface IArchivoRepository extends JpaRepository<Archivo, Integer> {

    List<Archivo> findByIdUsuario_IdUsuarioOrderByFechaSubidaDesc(Integer idUsuario);
}
