package com.apirest.backend.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.ApiErrorDTO;
import com.apirest.backend.dto.ArchivoDescargaDTO;
import com.apirest.backend.service.IArchivoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/archivos")
@Tag(name = "Archivos")
public class ArchivoController {

    @Autowired
    IArchivoService archivoService;

    @GetMapping("/{id}")
    @Operation(summary = "Ver o descargar un archivo", description = "Devuelve el contenido del archivo. Por defecto se muestra en el navegador; con descargar=true se fuerza la descarga. Solo su dueño o un ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contenido del archivo."),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido."),
            @ApiResponse(responseCode = "403", description = "No eres el dueño del archivo ni administrador.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "El archivo no existe.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<Resource> obtenerArchivo(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") boolean descargar) {

        ArchivoDescargaDTO archivo = archivoService.obtenerParaDescarga(jwt.getSubject(), id);

        ContentDisposition disposicion = (descargar
                ? ContentDisposition.attachment()
                : ContentDisposition.inline())
                .filename(archivo.nombreOriginal(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.tipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                // Defensa en profundidad: el archivo es contenido de usuario, nunca debe ejecutarse
                .header("X-Content-Type-Options", "nosniff")
                .header("Content-Security-Policy", "sandbox; default-src 'none'")
                .body(archivo.recurso());
    }
}
