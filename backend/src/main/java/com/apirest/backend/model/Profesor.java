package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.AreaDocencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "profesor")
public class Profesor {
    
    @Id 
    protected Integer idProfesor;

    
    @Column(unique = true)
    @OneToOne
    @JoinColumn (name = "idUsuario")
    protected Usuario idUsuario;

    protected String nombre;
    protected String direccionRes;
    protected Date fechaNacimiento;

    @Column(unique = true)
    protected Integer cedulaCiudadana;

    protected String telefono;
    protected AreaDocencia areaDocencia;
    protected String observaciones;
}
