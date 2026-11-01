package com.apirest.backend.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable 
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Table (name = "estudianteAcudiente")
public class EstudianteAcudiente {
    

    @ManyToOne
    @JoinColumn (name = "idEstudiante")
    protected Estudiante idEstudiante;
    @ManyToOne
    @JoinColumn (name = "idAcudiente")
    protected Acudiente idAcudiente;

    protected String parentesco;
    protected boolean contactoPrincipal;
}
