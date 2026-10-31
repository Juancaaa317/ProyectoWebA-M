package com.apirest.backend.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
