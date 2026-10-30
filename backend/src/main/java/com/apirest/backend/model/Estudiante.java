package com.apirest.backend.model;

import com.apirest.backend.Enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity 
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Table (name = "estudiante")
public class Estudiante {
    
    @Id 
    protected Integer idEstudiante;
    

    protected String nombre;
    protected Integer edad;
    @Column(unique = true)
    protected Integer tarjetaIdentidad;
    protected String colegio;
    protected Rol rol;
}
