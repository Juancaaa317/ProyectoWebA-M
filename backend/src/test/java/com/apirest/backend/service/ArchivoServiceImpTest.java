package com.apirest.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.ArchivoDescargaDTO;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.exception.TipoArchivoNoPermitidoException;
import com.apirest.backend.model.Archivo;
import com.apirest.backend.model.Usuario;
import com.apirest.backend.repository.IArchivoRepository;
import com.apirest.backend.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ArchivoServiceImpTest {

    @Mock
    IArchivoRepository archivoRepository;

    @Mock
    IUsuarioRepository usuarioRepository;

    @Mock
    IAlmacenamientoService almacenamiento;

    @InjectMocks
    ArchivoServiceImp archivoService;

    private Usuario dueno;
    private MockMultipartFile pdf;

    @BeforeEach
    void preparar() {
        dueno = usuario(1, "ana@correo.com", Rol.ESTUDIANTE);
        pdf = new MockMultipartFile("archivo", "../../recibo.pdf", "application/pdf",
                "%PDF-1.4".getBytes(StandardCharsets.US_ASCII));
    }

    private Usuario usuario(Integer id, String correo, Rol rol) {
        Usuario u = new Usuario();
        u.setIdUsuario(id);
        u.setCorreo(correo);
        u.setRol(rol);
        return u;
    }

    private Archivo archivoDe(Usuario propietario) {
        Archivo a = new Archivo();
        a.setIdArchivo(10);
        a.setIdUsuario(propietario);
        a.setNombreOriginal("recibo.pdf");
        a.setRuta("comprobantes/abc.pdf");
        a.setTipoMime("application/pdf");
        return a;
    }

    // ---------- registrar ----------

    @Test
    void registrarGuardaEnDiscoYEnBaseDeDatos() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(dueno));
        when(almacenamiento.guardar(pdf, "comprobantes"))
                .thenReturn(new ArchivoGuardado("comprobantes/abc.pdf", "application/pdf", 8L));
        when(archivoRepository.save(any(Archivo.class))).thenAnswer(inv -> inv.getArgument(0));

        Archivo resultado = archivoService.registrar("ana@correo.com", pdf, "comprobantes");

        assertSame(dueno, resultado.getIdUsuario());
        assertEquals("recibo.pdf", resultado.getNombreOriginal()); // sin "../../"
        assertEquals("comprobantes/abc.pdf", resultado.getRuta());
        assertEquals("application/pdf", resultado.getTipoMime());
        assertEquals(8L, resultado.getTamanoBytes());
    }

    @Test
    void registrarBorraElArchivoDelDiscoSiFallaLaBaseDeDatos() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(dueno));
        when(almacenamiento.guardar(pdf, "comprobantes"))
                .thenReturn(new ArchivoGuardado("comprobantes/abc.pdf", "application/pdf", 8L));
        when(archivoRepository.save(any(Archivo.class))).thenThrow(new IllegalStateException("BD caída"));

        assertThrows(IllegalStateException.class,
                () -> archivoService.registrar("ana@correo.com", pdf, "comprobantes"));

        verify(almacenamiento).eliminar("comprobantes/abc.pdf");
    }

    @Test
    void registrarNoTocaLaBaseDeDatosSiElArchivoNoEsValido() {
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(dueno));
        when(almacenamiento.guardar(pdf, "comprobantes"))
                .thenThrow(new TipoArchivoNoPermitidoException("no permitido"));

        assertThrows(TipoArchivoNoPermitidoException.class,
                () -> archivoService.registrar("ana@correo.com", pdf, "comprobantes"));

        verify(archivoRepository, never()).save(any());
    }

    @Test
    void registrarFallaSiElUsuarioNoExiste() {
        when(usuarioRepository.findByCorreo("nadie@correo.com")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> archivoService.registrar("nadie@correo.com", pdf, "comprobantes"));

        verifyNoInteractions(almacenamiento);
    }

    // ---------- obtenerParaDescarga ----------

    @Test
    void elDuenoPuedeDescargarSuArchivo() {
        Resource contenido = new ByteArrayResource(new byte[] { 1 });
        when(archivoRepository.findById(10)).thenReturn(Optional.of(archivoDe(dueno)));
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(dueno));
        when(almacenamiento.cargar("comprobantes/abc.pdf")).thenReturn(contenido);

        ArchivoDescargaDTO descarga = archivoService.obtenerParaDescarga("ana@correo.com", 10);

        assertSame(contenido, descarga.recurso());
        assertEquals("recibo.pdf", descarga.nombreOriginal());
        assertEquals("application/pdf", descarga.tipoMime());
    }

    @Test
    void unAdministradorPuedeDescargarArchivosAjenos() {
        Usuario admin = usuario(2, "admin@correo.com", Rol.ADMINISTRADOR);
        when(archivoRepository.findById(10)).thenReturn(Optional.of(archivoDe(dueno)));
        when(usuarioRepository.findByCorreo("admin@correo.com")).thenReturn(Optional.of(admin));
        when(almacenamiento.cargar("comprobantes/abc.pdf")).thenReturn(new ByteArrayResource(new byte[] { 1 }));

        ArchivoDescargaDTO descarga = archivoService.obtenerParaDescarga("admin@correo.com", 10);

        assertEquals("recibo.pdf", descarga.nombreOriginal());
    }

    @Test
    void otroEstudianteNoPuedeDescargarElArchivo() {
        Usuario otro = usuario(3, "luis@correo.com", Rol.ESTUDIANTE);
        when(archivoRepository.findById(10)).thenReturn(Optional.of(archivoDe(dueno)));
        when(usuarioRepository.findByCorreo("luis@correo.com")).thenReturn(Optional.of(otro));

        assertThrows(AccessDeniedException.class,
                () -> archivoService.obtenerParaDescarga("luis@correo.com", 10));

        verifyNoInteractions(almacenamiento);
    }

    @Test
    void descargarUnArchivoInexistenteDaNoEncontrado() {
        when(archivoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> archivoService.obtenerParaDescarga("ana@correo.com", 99));
    }
}
