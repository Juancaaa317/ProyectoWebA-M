package com.apirest.backend.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apirest.backend.Enums.EstadoPago;
import com.apirest.backend.model.Pago;

import jakarta.persistence.LockModeType;

public interface IPagoRepository extends JpaRepository<Pago, Integer> {

    // El EntityGraph trae estudiante y archivo en la misma consulta (evita una consulta extra por cada pago)
    @EntityGraph(attributePaths = { "idEstudiante", "idArchivo" })
    List<Pago> findByIdEstudiante_IdEstudianteOrderByFechaPagoDescIdPagoDesc(Integer idEstudiante);

    // Consulta del administrador: filtros opcionales por estado y periodo (null = sin filtrar)
    @EntityGraph(attributePaths = { "idEstudiante", "idArchivo" })
    @Query("SELECT p FROM Pago p "
            + "WHERE (:estado IS NULL OR p.estado = :estado) "
            + "AND (:periodo IS NULL OR p.periodo = :periodo) "
            + "ORDER BY p.fechaPago DESC, p.idPago DESC")
    List<Pago> buscar(@Param("estado") EstadoPago estado, @Param("periodo") String periodo);

    // Lee el pago bloqueando su fila hasta el fin de la transacción: dos administradores no pueden resolverlo a la vez
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pago p WHERE p.idPago = :idPago")
    Optional<Pago> findByIdForUpdate(@Param("idPago") Integer idPago);

    // ¿Ya hay un pago vigente (pendiente o aprobado) de ese estudiante para ese mes?
    boolean existsByIdEstudiante_IdEstudianteAndPeriodoAndEstadoIn(
            Integer idEstudiante, String periodo, Collection<EstadoPago> estados);
}
