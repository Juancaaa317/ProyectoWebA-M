package com.apirest.backend.model;


import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
@Data 
@Table (name = "horario")
public class Horario {
    
    @Id 
    protected Integer idHorario;

    @ManyToOne 
    @JoinColumn (name = "idClase")
    protected Clase idClase;
    protected Time horaInicio;
    protected Time horaFin;


}
