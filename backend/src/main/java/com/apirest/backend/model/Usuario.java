package com.apirest.backend.model;

import com.apirest.backend.Enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "usuario")
public class Usuario {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idUsuario;
    @Column(unique = true)
    protected String correo;

    @ToString.Exclude
    protected String contrasena;
    
    @Enumerated(EnumType.STRING)
    protected Rol rol;

}
