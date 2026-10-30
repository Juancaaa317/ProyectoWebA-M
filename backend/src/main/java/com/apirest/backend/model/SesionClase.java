package com.apirest.backend.model;

import java.sql.Time;

import com.apirest.backend.Enums.EstadoSesion;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@Entity 
@AllArgsConstructor 
@NoArgsConstructor 
@Table (name = "sesionClase")
public class SesionClase {
    @Id 
    protected Integer idSesion;

    @ManyToOne
    @JoinColumn (name = "idClase")
    protected Clase idClase;

    protected Time horaInicio;
    protected Time horaFin;
    protected EstadoSesion estadoSesion;
}
