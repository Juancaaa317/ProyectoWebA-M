package com.apirest.backend.model;


import com.apirest.backend.Enums.EstadoAsistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor 
@NoArgsConstructor 
@Data 
@Table (name = "asistencia")
public class Asistencia {
    @Id 
    protected Integer idAsistencia;
    
    @ManyToOne
    @JoinColumn (name = "idInscripcion")
    @Column(unique = true)
    protected Inscripcion idInscripcion;

    @ManyToOne
    @JoinColumn (name = "idSesion")
    @Column(unique = true)
    protected SesionClase idSesion;


    protected EstadoAsistencia estado;
    protected String observaciones;
}
