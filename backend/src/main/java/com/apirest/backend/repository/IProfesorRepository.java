package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apirest.backend.model.Profesor;

public interface IProfesorRepository extends JpaRepository<Profesor, Integer> {

    Optional<Profesor> findByIdUsuario_Correo(String correo);

    @Query("SELECT p FROM Profesor p JOIN FETCH p.idUsuario u "
                    + "WHERE :busqueda IS NULL "
                    + "OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
                    + "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
                    + "ORDER BY p.nombre")
    List<Profesor> buscarParaPanel(@Param("busqueda") String busqueda);
}
