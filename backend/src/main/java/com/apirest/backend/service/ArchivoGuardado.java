package com.apirest.backend.service;

/**
 * Resultado de guardar un archivo: lo que el servicio debe registrar en la tabla archivo.
 * El tipo MIME lo decide el servidor (según la extensión validada), no el cliente.
 */
public record ArchivoGuardado(String ruta, String tipoMime, long tamanoBytes) {
}
