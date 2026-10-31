package com.apirest.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Acudiente {
    
    @Id 
    protected Integer idAcudiente;
    protected String nombre;
    protected String direccionRes;
    protected String telefono;
    @Column(unique = true)
    protected String cedulaCiudadania;
}
