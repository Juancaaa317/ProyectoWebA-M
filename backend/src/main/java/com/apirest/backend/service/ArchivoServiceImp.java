package com.apirest.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.ArchivoDescargaDTO;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Archivo;
import com.apirest.backend.model.Usuario;
import com.apirest.backend.repository.IArchivoRepository;
import com.apirest.backend.repository.IUsuarioRepository;

@Service
public class ArchivoServiceImp implements IArchivoService {

    private static final int LARGO_MAXIMO_NOMBRE = 255;

    @Autowired
    IArchivoRepository archivoRepository;

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Autowired
    IAlmacenamientoService almacenamiento;

    @Override
    @Transactional
    public Archivo registrar(String correo, MultipartFile archivo, String carpeta) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario asociado a la sesión."));

        // 1) Primero el disco: si el archivo no es válido, no se toca la BD
        ArchivoGuardado guardado = almacenamiento.guardar(archivo, carpeta);

        // 2) Si la transacción termina en rollback (aquí o en quien nos llamó), borrar el archivo huérfano
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int estado) {
                    if (estado != STATUS_COMMITTED) {
                        almacenamiento.eliminar(guardado.ruta());
                    }
                }
            });
        }

        // 3) Después la BD, con los metadatos
        try {
            Archivo registro = new Archivo();
            registro.setIdUsuario(usuario);
            registro.setNombreOriginal(limpiarNombre(archivo.getOriginalFilename()));
            registro.setTamanoBytes(guardado.tamanoBytes());
            registro.setRuta(guardado.ruta());
            registro.setTipoMime(guardado.tipoMime());
            registro.setFechaSubida(LocalDateTime.now());
            return archivoRepository.save(registro);
        } catch (RuntimeException e) {
            almacenamiento.eliminar(guardado.ruta());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescargaDTO obtenerParaDescarga(String correo, Integer idArchivo) {
        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un archivo con el ID: " + idArchivo));

        Usuario solicitante = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario asociado a la sesión."));

        boolean esDueno = archivo.getIdUsuario().getIdUsuario().equals(solicitante.getIdUsuario());
        boolean esAdministrador = solicitante.getRol() == Rol.ADMINISTRADOR;
        if (!esDueno && !esAdministrador) {
            throw new AccessDeniedException("No puedes acceder a este archivo.");
        }

        Resource recurso = almacenamiento.cargar(archivo.getRuta());
        return new ArchivoDescargaDTO(recurso, archivo.getNombreOriginal(), archivo.getTipoMime());
    }

    // El nombre original solo se muestra: se deja sin rutas ni caracteres de control
    private String limpiarNombre(String nombre) {
        String limpio = nombre == null ? null : StringUtils.getFilename(StringUtils.cleanPath(nombre));
        if (limpio == null) {
            return "archivo";
        }
        limpio = limpio.replaceAll("\\p{Cntrl}", "").trim();
        if (limpio.isEmpty()) {
            return "archivo";
        }
        return limpio.length() > LARGO_MAXIMO_NOMBRE ? limpio.substring(0, LARGO_MAXIMO_NOMBRE) : limpio;
    }
}
