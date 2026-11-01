package com.apirest.backend.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Usuario;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IUsuarioRepository;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Autowired
    IEstudianteRepository estudianteRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Registrar estudiante
    @Override
    @Transactional
    public RegistroEstudianteRespuestaDTO registrarEstudiante(
            RegistroEstudianteDTO registro) {

        // Normalizar correo
        String correo = registro.getCorreo()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Comprobar si el correo ya existe
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con ese correo.");
        }

        // Comprobar que las contraseñas coincidan
        if (!registro.getContrasena().equals(
                registro.getConfirmarContrasena())) {

            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden.");
        }

        // Normalizar tarjeta de identidad
        String tarjetaIdentidad = registro.getTarjetaIdentidad();

        if (tarjetaIdentidad != null) {
            tarjetaIdentidad = tarjetaIdentidad.trim();

            if (tarjetaIdentidad.isEmpty()) {
                tarjetaIdentidad = null;
            }
        }

        // Comprobar si la tarjeta ya está registrada
        if (tarjetaIdentidad != null &&
                estudianteRepository.existsByTarjetaIdentidad(
                        tarjetaIdentidad)) {

            throw new IllegalArgumentException(
                    "La tarjeta de identidad ya está registrada.");
        }

        // Crear usuario
        Usuario usuario = new Usuario();

        usuario.setCorreo(correo);

        // Cifrar contraseña
        usuario.setContrasena(
                passwordEncoder.encode(registro.getContrasena()));

        // El registro público siempre tendrá rol ESTUDIANTE
        usuario.setRol(Rol.ESTUDIANTE);

        // Guardar usuario
        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        // Crear perfil del estudiante
        Estudiante estudiante = new Estudiante();

        estudiante.setIdUsuario(usuarioGuardado);
        estudiante.setNombre(registro.getNombre().trim());
        estudiante.setTarjetaIdentidad(tarjetaIdentidad);
        estudiante.setFechaNacimiento(
                registro.getFechaNacimiento());

        // Guardar estudiante
        Estudiante estudianteGuardado =
                estudianteRepository.save(estudiante);

        // Construir respuesta sin contraseña
        return new RegistroEstudianteRespuestaDTO(
                usuarioGuardado.getIdUsuario(),
                estudianteGuardado.getIdEstudiante(),
                estudianteGuardado.getNombre(),
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getRol()
        );
    }
}