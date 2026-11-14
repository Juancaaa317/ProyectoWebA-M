package com.apirest.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dónde se guarda el contenido de los archivos. Hoy hay una implementación en disco
 * (AlmacenamientoLocalServiceImp); más adelante puede haber otra para Supabase sin cambiar a quien la usa.
 */
public interface IAlmacenamientoService {

    // Valida y guarda el archivo dentro de la carpeta indicada. Devuelve la ruta relativa (con UUID).
    ArchivoGuardado guardar(MultipartFile archivo, String carpeta);

    // Devuelve el contenido a partir de la ruta relativa guardada en la BD.
    Resource cargar(String ruta);

    // Borra el archivo si existe. Es "mejor esfuerzo": se usa para limpiar si falla el registro en la BD.
    void eliminar(String ruta);
}
