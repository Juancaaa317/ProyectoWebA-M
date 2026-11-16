package com.apirest.backend.model;





import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity 
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Table (name = "estudiante")
public class Estudiante {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idEstudiante;

    
    @OneToOne 
    @JoinColumn (name = "idUsuario", unique = true)
    @ToString.Exclude
    protected Usuario idUsuario;
    

    protected String nombre;
    
    @Column(unique = true)
    protected String tarjetaIdentidad;

    protected Date fechaNacimiento;
}
