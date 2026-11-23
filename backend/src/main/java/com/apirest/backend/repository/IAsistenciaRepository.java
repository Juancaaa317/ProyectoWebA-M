package com.apirest.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apirest.backend.model.Asistencia;

public interface IAsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    @EntityGraph(attributePaths = { "idInscripcion", "idInscripcion.idEstudiante" })
    List<Asistencia> findByIdSesion_IdSesion(Integer idSesion);

    Optional<Asistencia> findByIdInscripcion_IdInscripcionAndIdSesion_IdSesion(
            Integer idInscripcion, Integer idSesion);

    @EntityGraph(attributePaths = { "idSesion", "idSesion.idClase", "idSesion.idClase.idPrograma" })
    @Query("""
            SELECT a FROM Asistencia a
            WHERE LOWER(a.idInscripcion.idEstudiante.idUsuario.correo) = LOWER(:correo)
            ORDER BY a.idSesion.fecha DESC, a.idSesion.horaInicio DESC
            """)
    List<Asistencia> buscarPorCorreoEstudiante(@Param("correo") String correo);
}
