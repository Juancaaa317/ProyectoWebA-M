package com.apirest.backend.model;

import com.apirest.backend.Enums.EstadoPrograma;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor 
@NoArgsConstructor 
@Data 
@Entity 
@Table (name = "programa")
public class Programa {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idPrograma;

    @Column(unique = true)
    protected String nombrePrograma;

    protected String descripcion;
    
    @Column(name = "estadoPrograma", nullable = false)
    @Enumerated(EnumType.STRING)
    protected  EstadoPrograma estadoPrograma;
    
}
