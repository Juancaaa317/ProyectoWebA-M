package com.apirest.backend.exception;

// 413: el archivo supera el tamaño máximo permitido
public class ArchivoDemasiadoGrandeException extends RuntimeException {

    public ArchivoDemasiadoGrandeException(String mensaje) {
        super(mensaje);
    }
}
