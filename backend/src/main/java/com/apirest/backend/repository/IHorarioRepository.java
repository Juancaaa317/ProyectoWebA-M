package com.apirest.backend.repository;

import java.sql.Time;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apirest.backend.Enums.DiaSemana;
import com.apirest.backend.model.Horario;

public interface IHorarioRepository extends JpaRepository<Horario, Integer> {

    // Clase y programa en la misma consulta (el DTO lee el nombre de ambos): evita una consulta extra por fila
    @Override
    @EntityGraph(attributePaths = { "idClase", "idClase.idPrograma" })
    List<Horario> findAll();

    @EntityGraph(attributePaths = { "idClase", "idClase.idPrograma" })
    List<Horario> findByIdClase_IdClase(Integer idClase);

    // ¿El profesor ya tiene una clase que se solape con [inicio, fin) ese día?
    // Dos franjas se solapan si la nueva empieza antes de que termine la existente y termina después de que empiece.
    // idActual excluye el propio horario cuando se está editando (null al crear).
    @Query("SELECT COUNT(h) > 0 FROM Horario h "
            + "WHERE h.idClase.idProfesor.idProfesor = :idProfesor "
            + "AND h.diaSemana = :dia "
            + "AND h.horaInicio < :fin AND h.horaFin > :inicio "
            + "AND (:idActual IS NULL OR h.idHorario <> :idActual)")
    boolean existeCruce(
            @Param("idProfesor") Integer idProfesor,
            @Param("dia") DiaSemana dia,
            @Param("inicio") Time inicio,
            @Param("fin") Time fin,
            @Param("idActual") Integer idActual);
}
