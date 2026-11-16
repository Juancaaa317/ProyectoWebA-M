package com.apirest.backend.service;

import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.dto.ArchivoDescargaDTO;
import com.apirest.backend.model.Archivo;

public interface IArchivoService {

    // Guarda el contenido en el almacenamiento y sus metadatos en la BD, asociados al usuario
    Archivo registrar(String correo, MultipartFile archivo, String carpeta);

    // Devuelve el archivo si el usuario es su dueño o administrador
    ArchivoDescargaDTO obtenerParaDescarga(String correo, Integer idArchivo);
}
