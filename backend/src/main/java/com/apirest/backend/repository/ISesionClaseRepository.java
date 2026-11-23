package com.apirest.backend.repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.model.SesionClase;

public interface ISesionClaseRepository extends JpaRepository<SesionClase, Integer> {

    // Clase y programa en la misma consulta (el DTO lee el nombre de ambos): evita una consulta extra por fila
    @EntityGraph(attributePaths = { "idClase", "idClase.idPrograma" })
    List<SesionClase> findByIdClase_IdClaseOrderByFechaAscHoraInicioAsc(Integer idClase);

    boolean existsByIdClase_IdClaseAndFechaAndHoraInicio(
            Integer idClase, Date fecha, Time horaInicio);
}
