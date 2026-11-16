package com.apirest.backend.service;

import java.util.Locale;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;
import com.apirest.backend.dto.LoginDTO;
import com.apirest.backend.dto.LoginRespuestaDTO;
import com.apirest.backend.dto.UsuarioSesionDTO;
import com.apirest.backend.dto.RegistroProfesorDTO;
import com.apirest.backend.dto.RegistroProfesorRespuestaDTO;
import com.apirest.backend.dto.EstudianteListadoDTO;
import com.apirest.backend.dto.ProfesorListadoDTO;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Profesor;
import com.apirest.backend.model.Usuario;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IProfesorRepository;
import com.apirest.backend.repository.IUsuarioRepository;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.CredencialesInvalidasException;
import com.apirest.backend.security.JwtService;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Autowired
    IEstudianteRepository estudianteRepository;

    @Autowired
    IProfesorRepository profesorRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

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
            throw new RecursoDuplicadoException(
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

            throw new RecursoDuplicadoException(
                    "La tarjeta de identidad ya está registrada.");
        }

        // Crear usuario
        Usuario usuario = new Usuario();

        usuario.setCorreo(correo);
        usuario.setNombre(registro.getNombre().trim());
        usuario.setTelefono(registro.getTelefono().trim());

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

    @Override
    @Transactional(readOnly = true)
    public LoginRespuestaDTO iniciarSesion(LoginDTO login) {
        String correo = login.getCorreo().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(login.getPassword(), usuario.getContrasena())) {
            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario);
        UsuarioSesionDTO sesion = new UsuarioSesionDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.getRol());

        return new LoginRespuestaDTO(token, sesion);
    }

    @Override
    @Transactional
    public RegistroProfesorRespuestaDTO registrarProfesor(
            RegistroProfesorDTO registro) {

        String correo = registro.getCorreo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un usuario registrado con ese correo.");
        }

        if (!registro.getContrasena().equals(
                registro.getConfirmarContrasena())) {
            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden.");
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setNombre(registro.getNombre().trim());
        String telefono = registro.getTelefono() == null
                || registro.getTelefono().isBlank()
                        ? null
                        : registro.getTelefono().trim();
        usuario.setTelefono(telefono);
        usuario.setContrasena(
                passwordEncoder.encode(registro.getContrasena()));
        usuario.setRol(Rol.PROFESOR);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Profesor profesor = new Profesor();
        profesor.setIdUsuario(usuarioGuardado);
        profesor.setNombre(usuarioGuardado.getNombre());
        profesor.setTelefono(usuarioGuardado.getTelefono());
        profesor.setAreaDocencia(registro.getAreaDocencia());

        Profesor profesorGuardado = profesorRepository.save(profesor);

        return new RegistroProfesorRespuestaDTO(
                usuarioGuardado.getIdUsuario(),
                profesorGuardado.getIdProfesor(),
                profesorGuardado.getNombre(),
                usuarioGuardado.getCorreo(),
                profesorGuardado.getAreaDocencia(),
                usuarioGuardado.getRol());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteListadoDTO> listarEstudiantes(String busqueda) {
        String filtro = normalizarFiltro(busqueda);
        return estudianteRepository.buscarParaPanel(filtro).stream()
                .map(estudiante -> new EstudianteListadoDTO(
                        estudiante.getIdEstudiante(),
                        estudiante.getNombre(),
                        estudiante.getTarjetaIdentidad(),
                        estudiante.getIdUsuario().getCorreo(),
                        estudiante.getIdUsuario().getTelefono()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorListadoDTO> listarProfesores(String busqueda) {
        String filtro = normalizarFiltro(busqueda);
        return profesorRepository.buscarParaPanel(filtro).stream()
                .map(profesor -> new ProfesorListadoDTO(
                        profesor.getIdProfesor(),
                        profesor.getNombre(),
                        profesor.getIdUsuario().getCorreo(),
                        profesor.getTelefono(),
                        profesor.getAreaDocencia()))
                .toList();
    }

    private String normalizarFiltro(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return null;
        }
        return busqueda.trim();
    }
}
