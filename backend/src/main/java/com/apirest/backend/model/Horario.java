package com.apirest.backend.model;


import java.sql.Time;

import com.apirest.backend.Enums.DiaSemana;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
@Data 
@Table (name = "horario")
public class Horario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idHorario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idClase", nullable = false)
    protected Clase idClase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected DiaSemana diaSemana;

    @Column(nullable = false)
    protected Time horaInicio;

    @Column(nullable = false)
    protected Time horaFin;


}
