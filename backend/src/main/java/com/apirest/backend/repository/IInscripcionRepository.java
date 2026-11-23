package com.apirest.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.sql.Date;

import com.apirest.backend.Enums.EstadoInscripcion;
import com.apirest.backend.model.Inscripcion;

public interface IInscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    // Estudiante, clase y programa en la misma consulta (el DTO lee el nombre de los tres): evita una consulta extra por fila
    @Override
    @EntityGraph(attributePaths = { "idEstudiante", "idClase", "idClase.idPrograma" })
    List<Inscripcion> findAll();

    Optional<Inscripcion> findByIdEstudiante_IdEstudianteAndIdClase_IdClase(
            Integer idEstudiante, Integer idClase);

    @EntityGraph(attributePaths = { "idEstudiante", "idClase", "idClase.idPrograma" })
    List<Inscripcion> findByIdEstudiante_IdEstudianteOrderByFechaInscripcionDesc(
            Integer idEstudiante);

    long countByIdClase_IdClaseAndEstadoInscripcionIn(
            Integer idClase, List<EstadoInscripcion> estados);

    List<Inscripcion> findByIdClase_IdClaseAndEstadoInscripcion(
            Integer idClase, EstadoInscripcion estado);

    @EntityGraph(attributePaths = { "idEstudiante" })
    List<Inscripcion> findByIdClase_IdClaseAndFechaInscripcionLessThanEqual(
            Integer idClase, Date fechaInscripcion);
}
