package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apirest.backend.model.Profesor;

import jakarta.persistence.LockModeType;

public interface IProfesorRepository extends JpaRepository<Profesor, Integer> {

    Optional<Profesor> findByIdUsuario_Correo(String correo);

    // Bloquea la fila del profesor hasta el fin de la transacción: serializa los cambios de horario de un mismo profesor
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Profesor p WHERE p.idProfesor = :idProfesor")
    Optional<Profesor> findByIdForUpdate(@Param("idProfesor") Integer idProfesor);

    @Query("SELECT p FROM Profesor p JOIN FETCH p.idUsuario u "
                    + "WHERE :busqueda IS NULL "
                    + "OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
                    + "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
                    + "ORDER BY p.nombre")
    List<Profesor> buscarParaPanel(@Param("busqueda") String busqueda);
}
