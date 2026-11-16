package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.EstadoInscripcion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table (name = "inscripcion")
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idInscripcion;

    @ManyToOne
    @JoinColumn (name = "idEstudiante")
    protected Estudiante idEstudiante;

    @ManyToOne
    @JoinColumn (name = "idClase")
    protected Clase idClase;

    protected Date fechaInscripcion;
    
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    protected EstadoInscripcion estadoInscripcion;
}
