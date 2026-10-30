package com.apirest.backend.model;

import com.apirest.backend.Enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

@Table(name = "usuario")
public class Usuario {
    @Id
    protected Integer idUsuario;
    @Column(unique = true)
    protected String correo;
    protected String contrasena;
    @Enumerated(EnumType.STRING)
    protected Rol rol;

}
