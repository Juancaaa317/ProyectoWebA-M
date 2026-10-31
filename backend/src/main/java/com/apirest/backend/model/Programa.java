package com.apirest.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    protected Integer idPrograma;

    @Column(unique = true)
    protected String nombrePrograma;

    protected String descripcion;
    protected boolean estado;
}
