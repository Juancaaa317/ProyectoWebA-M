package com.apirest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import com.apirest.backend.model.Clase;

public interface IClaseRepository extends JpaRepository<Clase, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Clase c WHERE c.idClase = :idClase")
    java.util.Optional<Clase> findByIdForUpdate(@Param("idClase") Integer idClase);
}
