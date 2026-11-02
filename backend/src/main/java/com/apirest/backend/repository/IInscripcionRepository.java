package com.apirest.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.sql.Date;

import com.apirest.backend.Enums.EstadoInscripcion;
import com.apirest.backend.model.Inscripcion;

public interface IInscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    Optional<Inscripcion> findByIdEstudiante_IdEstudianteAndIdClase_IdClase(
            Integer idEstudiante, Integer idClase);

    List<Inscripcion> findByIdEstudiante_IdEstudianteOrderByFechaInscripcionDesc(
            Integer idEstudiante);

    long countByIdClase_IdClaseAndEstadoInscripcionIn(
            Integer idClase, List<EstadoInscripcion> estados);

    List<Inscripcion> findByIdClase_IdClaseAndEstadoInscripcion(
            Integer idClase, EstadoInscripcion estado);

    List<Inscripcion> findByIdClase_IdClaseAndFechaInscripcionLessThanEqual(
            Integer idClase, Date fechaInscripcion);
}
