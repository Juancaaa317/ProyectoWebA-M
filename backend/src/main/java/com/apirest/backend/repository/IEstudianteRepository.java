package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apirest.backend.model.Estudiante;

import jakarta.persistence.LockModeType;

public interface IEstudianteRepository extends JpaRepository<Estudiante, Integer> {

        boolean existsByTarjetaIdentidad(String tarjetaIdentidad);
        Optional<Estudiante> findByIdUsuario_Correo(String correo);

        // Bloquea la fila del estudiante hasta el fin de la transacción: serializa sus pagos simultáneos
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT e FROM Estudiante e WHERE e.idUsuario.correo = :correo")
        Optional<Estudiante> findByCorreoForUpdate(@Param("correo") String correo);

        @Query("SELECT e FROM Estudiante e JOIN FETCH e.idUsuario u "
                        + "WHERE :busqueda IS NULL "
                        + "OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
                        + "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :busqueda, '%')) "
                        + "OR e.tarjetaIdentidad LIKE CONCAT('%', :busqueda, '%') "
                        + "ORDER BY e.nombre")
        List<Estudiante> buscarParaPanel(@Param("busqueda") String busqueda);

}
