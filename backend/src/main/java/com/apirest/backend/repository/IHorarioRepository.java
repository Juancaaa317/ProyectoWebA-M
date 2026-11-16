package com.apirest.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.Horario;

public interface IHorarioRepository extends JpaRepository<Horario, Integer> {

    List<Horario> findByIdClase_IdClase(Integer idClase);
}
