package com.apirest.backend.model;

import java.sql.Date;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
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

    
    @OneToOne 
    @JoinColumn (name = "idUsuario")
    @Column(unique = true)
    protected Usuario idUsuario;
    

    protected String nombre;
    
    @Column(unique = true)
    protected Integer tarjetaIdentidad;

    protected Date fechaNacimiento;
}
