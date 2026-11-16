package com.apirest.backend.exception;

// 415: extensión no permitida o contenido que no corresponde a la extensión
public class TipoArchivoNoPermitidoException extends RuntimeException {

    public TipoArchivoNoPermitidoException(String mensaje) {
        super(mensaje);
    }
}
