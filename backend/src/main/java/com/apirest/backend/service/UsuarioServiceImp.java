package com.apirest.backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.AreaDocencia;
import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.ActualizarPerfilDTO;
import com.apirest.backend.dto.CambiarContrasenaDTO;
import com.apirest.backend.dto.PerfilDTO;
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
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.exception.CredencialesInvalidasException;
import com.apirest.backend.security.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImp implements IUsuarioService {

    // Límite del algoritmo BCrypt, en bytes
    private static final int MAX_BYTES_CONTRASENA = 72;

    private final IUsuarioRepository usuarioRepository;

    private final IEstudianteRepository estudianteRepository;

    private final IProfesorRepository profesorRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

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

        validarLargoContrasena(registro.getContrasena());

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

        validarLargoContrasena(registro.getContrasena());

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

    @Override
    @Transactional(readOnly = true)
    public PerfilDTO obtenerPerfil(String correo) {
        return convertirPerfil(buscarUsuario(correo));
    }

    // El nombre (y el teléfono del profesor) también están en la tabla del rol: se actualizan juntos
    // en la misma transacción para que no queden distintos. Si no se envía teléfono, el actual no cambia.
    @Override
    @Transactional
    public PerfilDTO actualizarPerfil(String correo, ActualizarPerfilDTO datos) {
        Usuario usuario = buscarUsuario(correo);
        String nombre = datos.getNombre().trim();
        String telefono = datos.getTelefono() == null ? null : datos.getTelefono().trim();

        usuario.setNombre(nombre);
        if (telefono != null) {
            usuario.setTelefono(telefono);
        }
        usuarioRepository.save(usuario);

        if (usuario.getRol() == Rol.ESTUDIANTE) {
            Estudiante estudiante = buscarEstudiante(correo);
            estudiante.setNombre(nombre);
            estudianteRepository.save(estudiante);
        } else if (usuario.getRol() == Rol.PROFESOR) {
            Profesor profesor = buscarProfesor(correo);
            profesor.setNombre(nombre);
            if (telefono != null) {
                profesor.setTelefono(telefono);
            }
            profesorRepository.save(profesor);
        }

        return convertirPerfil(usuario);
    }

    @Override
    @Transactional
    public void cambiarContrasena(String correo, CambiarContrasenaDTO datos) {
        Usuario usuario = buscarUsuario(correo);

        if (!datos.getContrasenaNueva().equals(datos.getConfirmarContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        validarLargoContrasena(datos.getContrasenaNueva());

        // Es un 400 y no un 401 a propósito: el frontend cierra la sesión ante un 401, y equivocarse
        // al escribir la contraseña actual no debe desconectar al usuario.
        if (!passwordEncoder.matches(datos.getContrasenaActual(), usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        usuario.setContrasena(passwordEncoder.encode(datos.getContrasenaNueva()));
        usuarioRepository.save(usuario);
    }

    // BCrypt solo admite 72 BYTES (no caracteres): las tildes y la ñ ocupan 2. Sin esta comprobación el
    // usuario recibiría el mensaje técnico en inglés del encoder ("password cannot be more than 72 bytes").
    private void validarLargoContrasena(String contrasena) {
        if (contrasena.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña no puede superar los 72 bytes (las tildes y la ñ cuentan como 2).");
        }
    }

    private Usuario buscarUsuario(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario asociado a la sesión."));
    }

    private Estudiante buscarEstudiante(String correo) {
        return estudianteRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de estudiante asociado a la sesión."));
    }

    private Profesor buscarProfesor(String correo) {
        return profesorRepository.findByIdUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el perfil de profesor asociado a la sesión."));
    }

    private PerfilDTO convertirPerfil(Usuario usuario) {
        String tarjetaIdentidad = null;
        Date fechaNacimiento = null;
        AreaDocencia areaDocencia = null;

        if (usuario.getRol() == Rol.ESTUDIANTE) {
            Estudiante estudiante = buscarEstudiante(usuario.getCorreo());
            tarjetaIdentidad = estudiante.getTarjetaIdentidad();
            fechaNacimiento = estudiante.getFechaNacimiento();
        } else if (usuario.getRol() == Rol.PROFESOR) {
            areaDocencia = buscarProfesor(usuario.getCorreo()).getAreaDocencia();
        }

        return new PerfilDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getRol(),
                tarjetaIdentidad,
                fechaNacimiento,
                areaDocencia);
    }

    private String normalizarFiltro(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return null;
        }
        return busqueda.trim();
    }
}
