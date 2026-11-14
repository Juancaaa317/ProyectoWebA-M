package com.apirest.backend.exception;

// 400: archivo ausente, vacío o con una ruta que no es válida
public class ArchivoInvalidoException extends RuntimeException {

    public ArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
