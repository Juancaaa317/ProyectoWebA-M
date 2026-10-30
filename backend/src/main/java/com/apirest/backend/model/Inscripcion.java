package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.Estado;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    protected Integer idInscripcion;
    protected Date fechaInscripcion;
    
    protected Estado estado;
}
