package com.apirest.backend.model;

import java.sql.Time;
import java.sql.Date;

import com.apirest.backend.Enums.EstadoSesion;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@Entity 
@AllArgsConstructor 
@NoArgsConstructor 
@Table(name = "sesion_clase")
public class SesionClase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idSesion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idClase", nullable = false)
    protected Clase idClase;

    @Column(nullable = false)
    protected Date fecha;

    @Column(nullable = false)
    protected Time horaInicio;

    @Column(nullable = false)
    protected Time horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    protected EstadoSesion estadoSesion;
}
