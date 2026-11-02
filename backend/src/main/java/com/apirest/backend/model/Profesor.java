package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.AreaDocencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table (name = "profesor")
@AllArgsConstructor 
@NoArgsConstructor 
@Data 
public class Profesor {
    
    @Id 
    protected Integer idProfesor;

    
    
    @OneToOne
    @JoinColumn (name = "idUsuario",  unique = true)
    protected Usuario idUsuario;

    protected String nombre;
    protected String direccionRes;
    protected Date fechaNacimiento;

    @Column(unique = true)
    protected String cedulaCiudadana;

    protected String telefono;
    
    @Enumerated(EnumType.STRING)
    protected AreaDocencia areaDocencia;
    protected String observaciones;
}
