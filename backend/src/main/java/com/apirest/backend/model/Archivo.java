package com.apirest.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadatos de un archivo subido. El contenido vive en el sistema de archivos
 * (o en Supabase más adelante); aquí solo se guarda su descripción.
 */
@Data @Entity @AllArgsConstructor @NoArgsConstructor
@Table(name = "archivo")
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idArchivo;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    protected Usuario idUsuario;

    protected String nombreOriginal;

    protected Long tamanoBytes;

    // Ruta relativa con nombre UUID, ej. comprobantes/3f2a...44.pdf
    protected String ruta;

    protected String tipoMime;

    protected LocalDateTime fechaSubida;
}
