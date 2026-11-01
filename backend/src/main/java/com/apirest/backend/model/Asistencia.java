package com.apirest.backend.model;

import com.apirest.backend.Enums.EstadoAsistencia;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "asistencia")
public class Asistencia {
    @Id
    protected Integer idAsistencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idInscripcion", nullable = false)
    protected Inscripcion idInscripcion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSesion", nullable = false)
    protected SesionClase idSesion;

    @Enumerated(EnumType.STRING)
    protected EstadoAsistencia estado;
    protected String observaciones;
}
