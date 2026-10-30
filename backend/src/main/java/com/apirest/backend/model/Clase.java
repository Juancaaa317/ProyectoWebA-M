package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.Programa;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    
    protected Date fechaInicio;
    protected Date fechaFinalizacion;
    protected Programa programa;
    protected String nombre;

    

   
}
