package com.apirest.backend.dto;

import org.springframework.core.io.Resource;

// Lo necesario para entregar un archivo: el contenido y cómo presentarlo al usuario
public record ArchivoDescargaDTO(Resource recurso, String nombreOriginal, String tipoMime) {
}
