package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.EstadoClase;



import jakarta.persistence.Entity;
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
@Table (name = "clase")
public class Clase {
    
    @Id 
    protected Integer idClase;
    
    @ManyToOne 
    @JoinColumn (name = "idProfesor")
    protected Profesor idProfesor;

    @ManyToOne 
    @JoinColumn (name = "idPrograma")
    protected Programa idPrograma;
    
    protected Date fechaInicio;
    protected Date fechaFinalizacion;
    protected String nombre;
    protected Integer cupoMaximo;
    protected EstadoClase estadoClase;

    

   
}
