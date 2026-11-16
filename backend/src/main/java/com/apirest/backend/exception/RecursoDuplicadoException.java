package com.apirest.backend.exception;

public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}