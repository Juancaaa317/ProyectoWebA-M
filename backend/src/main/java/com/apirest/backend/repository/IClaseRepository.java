package com.apirest.backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.model.Clase;

public interface IClaseRepository extends JpaRepository<Clase, Integer> {

    // Trae profesor y programa en la misma consulta (el DTO los lee de cada clase): evita una consulta extra por fila
    @Override
    @EntityGraph(attributePaths = { "idProfesor", "idPrograma" })
    List<Clase> findAll();

    // Clases en ciertos estados, con profesor y programa en la misma consulta (vista pública de profesores)
    @EntityGraph(attributePaths = { "idProfesor", "idPrograma" })
    List<Clase> findByEstadoClaseIn(Collection<EstadoClase> estados);

    // Clases asignadas a un profesor (vista "Mis clases")
    @EntityGraph(attributePaths = { "idProfesor", "idPrograma" })
    List<Clase> findByIdProfesor_IdProfesorOrderByNombreAsc(Integer idProfesor);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Clase c WHERE c.idClase = :idClase")
    java.util.Optional<Clase> findByIdForUpdate(@Param("idClase") Integer idClase);
}
