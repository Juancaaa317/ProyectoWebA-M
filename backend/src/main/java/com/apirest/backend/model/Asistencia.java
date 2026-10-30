package com.apirest.backend.model;

import java.sql.Date;

import com.apirest.backend.Enums.EstadoAsistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    protected Date fechaAsistencia;
    protected EstadoAsistencia estado;
}
