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
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Table(name = "usuario")
public class Usuario {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idUsuario;

    @Column(unique = true)
    protected String correo;

    @Column(length = 150)
    protected String nombre;

    @Column(length = 30)
    protected String telefono;

    protected String contrasena;
    
    @Enumerated(EnumType.STRING)
    protected Rol rol;

}
