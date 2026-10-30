package com.apirest.backend.model;

import com.apirest.backend.Enums.AreaDocencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity 
@Table (name = "profesor")
public class Profesor {
    
    @Id 
    protected Integer idProfesor;

    @JoinColumn (name = "idUsuario")
    protected Usuario usuario;
    protected String nombre;
    protected String direccionRes;
    protected Integer edad;
    @Column(unique = true)
    protected Integer cedulaCiudadana;
    protected String telefono;
    protected AreaDocencia areaDocencia;
    protected String observaciones;
}
