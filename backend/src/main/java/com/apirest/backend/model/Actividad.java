package com.apirest.backend.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@NoArgsConstructor 
@AllArgsConstructor 
@Data 
@Entity 
@Table (name = "actividad")
public class Actividad {
    @Id 
    protected Integer idActividad;
    @ManyToOne 
    @JoinColumn (name = "idUsuario")
    protected Usuario idUsuario;
    protected String accion;
    protected String entidad;
    protected Integer idReferencia;
    protected Date fecha;
}
