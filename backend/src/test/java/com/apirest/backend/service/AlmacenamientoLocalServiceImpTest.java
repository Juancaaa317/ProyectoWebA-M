package com.apirest.backend.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import com.apirest.backend.exception.ArchivoDemasiadoGrandeException;
import com.apirest.backend.exception.ArchivoInvalidoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.exception.TipoArchivoNoPermitidoException;

// Usa una carpeta temporal real: no toca MySQL ni la carpeta uploads del proyecto
class AlmacenamientoLocalServiceImpTest {

    private static final long MAX = 1024; // 1 KB para poder probar el límite sin archivos grandes
    private static final Pattern RUTA_UUID = Pattern.compile(
            "comprobantes/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.pdf");

    @TempDir
    Path carpeta;

    AlmacenamientoLocalServiceImp almacenamiento;

    @BeforeEach
    void preparar() {
        almacenamiento = new AlmacenamientoLocalServiceImp(carpeta.toString(), MAX);
    }

    private MockMultipartFile pdf(String nombre) {
        return new MockMultipartFile("archivo", nombre, "application/pdf",
                "%PDF-1.4 contenido de prueba".getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    void guardaUnPdfValidoConNombreUuid() throws IOException {
        ArchivoGuardado resultado = almacenamiento.guardar(pdf("recibo octubre.pdf"), "comprobantes");

        assertTrue(RUTA_UUID.matcher(resultado.ruta()).matches(), "ruta inesperada: " + resultado.ruta());
        assertEquals("application/pdf", resultado.tipoMime());
        assertTrue(Files.exists(carpeta.resolve(resultado.ruta())));
        assertArrayEquals(
                "%PDF-1.4 contenido de prueba".getBytes(StandardCharsets.US_ASCII),
                Files.readAllBytes(carpeta.resolve(resultado.ruta())));
    }

    @Test
    void dosSubidasConElMismoNombreGeneranRutasDistintas() {
        ArchivoGuardado a = almacenamiento.guardar(pdf("recibo.pdf"), "comprobantes");
        ArchivoGuardado b = almacenamiento.guardar(pdf("recibo.pdf"), "comprobantes");

        assertNotEquals(a.ruta(), b.ruta());
    }

    @Test
    void elNombreOriginalNuncaFormaParteDeLaRuta() {
        ArchivoGuardado resultado = almacenamiento.guardar(pdf("../../evil.pdf"), "comprobantes");

        assertTrue(RUTA_UUID.matcher(resultado.ruta()).matches());
        assertTrue(Files.exists(carpeta.resolve(resultado.ruta())));
    }

    @Test
    void aceptaLaExtensionEnMayusculas() {
        MockMultipartFile png = new MockMultipartFile("archivo", "FOTO.PNG", "image/png",
                new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A });

        ArchivoGuardado resultado = almacenamiento.guardar(png, "comprobantes");

        assertTrue(resultado.ruta().endsWith(".png"));
        assertEquals("image/png", resultado.tipoMime());
    }

    @Test
    void rechazaExtensionNoPermitida() {
        MockMultipartFile exe = new MockMultipartFile("archivo", "virus.exe", "application/pdf",
                "%PDF-1.4".getBytes(StandardCharsets.US_ASCII));

        assertThrows(TipoArchivoNoPermitidoException.class,
                () -> almacenamiento.guardar(exe, "comprobantes"));
    }

    @Test
    void rechazaArchivoSinExtension() {
        MockMultipartFile sinExtension = new MockMultipartFile("archivo", "recibo", "application/pdf",
                "%PDF-1.4".getBytes(StandardCharsets.US_ASCII));

        assertThrows(TipoArchivoNoPermitidoException.class,
                () -> almacenamiento.guardar(sinExtension, "comprobantes"));
    }

    @Test
    void rechazaContenidoQueNoCorrespondeALaExtension() {
        MockMultipartFile falso = new MockMultipartFile("archivo", "falso.pdf", "application/pdf",
                "MZ esto es un ejecutable".getBytes(StandardCharsets.US_ASCII));

        assertThrows(TipoArchivoNoPermitidoException.class,
                () -> almacenamiento.guardar(falso, "comprobantes"));
    }

    @Test
    void rechazaArchivoVacio() {
        MockMultipartFile vacio = new MockMultipartFile("archivo", "vacio.pdf", "application/pdf", new byte[0]);

        assertThrows(ArchivoInvalidoException.class, () -> almacenamiento.guardar(vacio, "comprobantes"));
    }

    @Test
    void rechazaArchivoMasGrandeQueElMaximo() {
        byte[] grande = new byte[(int) MAX + 1];
        System.arraycopy("%PDF".getBytes(StandardCharsets.US_ASCII), 0, grande, 0, 4);
        MockMultipartFile archivo = new MockMultipartFile("archivo", "grande.pdf", "application/pdf", grande);

        assertThrows(ArchivoDemasiadoGrandeException.class, () -> almacenamiento.guardar(archivo, "comprobantes"));
    }

    @Test
    void rechazaCarpetaConRecorridoDeDirectorios() {
        assertThrows(IllegalArgumentException.class, () -> almacenamiento.guardar(pdf("a.pdf"), "../fuera"));
    }

    @Test
    void cargaUnArchivoGuardado() throws IOException {
        ArchivoGuardado guardado = almacenamiento.guardar(pdf("a.pdf"), "comprobantes");

        Resource recurso = almacenamiento.cargar(guardado.ruta());

        assertTrue(recurso.exists());
        assertArrayEquals(
                "%PDF-1.4 contenido de prueba".getBytes(StandardCharsets.US_ASCII),
                recurso.getInputStream().readAllBytes());
    }

    @Test
    void cargarUnArchivoInexistenteDaNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class,
                () -> almacenamiento.cargar("comprobantes/no-existe.pdf"));
    }

    @Test
    void cargarNoPermiteSalirDeLaCarpetaBase() throws IOException {
        Path fuera = Files.createTempFile("secreto", ".txt");
        try {
            String relativa = "../" + fuera.getFileName();
            assertThrows(ArchivoInvalidoException.class, () -> almacenamiento.cargar(relativa));
            assertThrows(ArchivoInvalidoException.class, () -> almacenamiento.cargar("../../etc/passwd"));
        } finally {
            Files.deleteIfExists(fuera);
        }
    }

    @Test
    void eliminaUnArchivoGuardado() {
        ArchivoGuardado guardado = almacenamiento.guardar(pdf("a.pdf"), "comprobantes");

        almacenamiento.eliminar(guardado.ruta());

        assertFalse(Files.exists(carpeta.resolve(guardado.ruta())));
    }

    @Test
    void eliminarUnArchivoInexistenteNoFalla() {
        almacenamiento.eliminar("comprobantes/no-existe.pdf");
    }
}
