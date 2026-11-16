package com.apirest.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.exception.ArchivoDemasiadoGrandeException;
import com.apirest.backend.exception.ArchivoInvalidoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.exception.TipoArchivoNoPermitidoException;

/**
 * Guarda los archivos en una carpeta del servidor. El nombre en disco es siempre
 * un UUID generado aquí; el nombre que envía el usuario nunca se usa como ruta.
 */
@Service
public class AlmacenamientoLocalServiceImp implements IAlmacenamientoService {

    private static final Logger logger = LoggerFactory.getLogger(AlmacenamientoLocalServiceImp.class);

    // Extensiones permitidas -> tipo MIME que asignamos nosotros
    private static final Map<String, String> TIPOS_MIME = Map.of(
            "pdf", "application/pdf",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg");

    // Extensiones permitidas -> primeros bytes que debe tener el contenido real (magic numbers)
    private static final Map<String, byte[]> FIRMAS = Map.of(
            "pdf", new byte[] { 0x25, 0x50, 0x44, 0x46 },                                   // %PDF
            "png", new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 },                            // .PNG
            "jpg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF },
            "jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF });

    private final Path base;
    private final long maxBytes;

    public AlmacenamientoLocalServiceImp(
            @Value("${app.storage.ruta}") String ruta,
            @Value("${app.storage.max-bytes}") long maxBytes) {
        this.base = Path.of(ruta).toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
    }

    @Override
    public ArchivoGuardado guardar(MultipartFile archivo, String carpeta) {
        if (carpeta == null || !carpeta.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Carpeta de almacenamiento no válida.");
        }
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoInvalidoException("Debes adjuntar un archivo.");
        }
        if (archivo.getSize() > maxBytes) {
            throw new ArchivoDemasiadoGrandeException(
                    "El archivo supera el tamaño máximo de " + (maxBytes / (1024 * 1024)) + " MB.");
        }

        String extension = extensionDe(archivo.getOriginalFilename());
        if (!TIPOS_MIME.containsKey(extension)) {
            throw new TipoArchivoNoPermitidoException(
                    "Tipo de archivo no permitido. Formatos aceptados: pdf, png, jpg, jpeg.");
        }

        verificarContenido(archivo, extension);

        String ruta = carpeta + "/" + UUID.randomUUID() + "." + extension;
        Path destino = resolver(ruta);

        try (InputStream entrada = archivo.getInputStream()) {
            Files.createDirectories(destino.getParent());
            // Sin REPLACE_EXISTING: si el archivo ya existiera, falla en vez de sobrescribir
            Files.copy(entrada, destino);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo.", e);
        }

        return new ArchivoGuardado(ruta, TIPOS_MIME.get(extension), archivo.getSize());
    }

    @Override
    public Resource cargar(String ruta) {
        Path archivo = resolver(ruta);
        if (!Files.isRegularFile(archivo) || !Files.isReadable(archivo)) {
            throw new RecursoNoEncontradoException("El archivo solicitado no existe.");
        }
        return new FileSystemResource(archivo);
    }

    @Override
    public void eliminar(String ruta) {
        try {
            Files.deleteIfExists(resolver(ruta));
        } catch (IOException | RuntimeException e) {
            // Es limpieza: no debe ocultar el error original que la provocó
            logger.warn("No se pudo eliminar el archivo {}", ruta, e);
        }
    }

    // Extensión en minúsculas, o cadena vacía si no hay
    private String extensionDe(String nombre) {
        if (nombre == null) {
            return "";
        }
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase();
    }

    // Compara los primeros bytes del archivo con la firma esperada para su extensión
    private void verificarContenido(MultipartFile archivo, String extension) {
        byte[] firma = FIRMAS.get(extension);
        try (InputStream entrada = archivo.getInputStream()) {
            byte[] cabecera = entrada.readNBytes(firma.length);
            if (!Arrays.equals(cabecera, firma)) {
                throw new TipoArchivoNoPermitidoException(
                        "El contenido del archivo no corresponde a su extensión ." + extension + ".");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo.", e);
        }
    }

    // Convierte una ruta relativa en ruta real, garantizando que no sale de la carpeta base
    private Path resolver(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            throw new ArchivoInvalidoException("Ruta de archivo no válida.");
        }
        Path resuelta = base.resolve(ruta).normalize();
        if (!resuelta.startsWith(base)) {
            throw new ArchivoInvalidoException("Ruta de archivo no válida.");
        }
        return resuelta;
    }
}
