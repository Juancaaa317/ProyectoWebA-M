package com.apirest.backend.model;

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
@Table (name = "acudiente")
public class Acudiente {
    
    @Id 
    protected Integer idAcudiente;
    protected String nombre;
    protected String direccionRes;
    protected String telefono;
    @Column(unique = true)
    protected String cedulaCiudadania;
}
