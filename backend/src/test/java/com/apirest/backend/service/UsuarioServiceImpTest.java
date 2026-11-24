package com.apirest.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.apirest.backend.Enums.Rol;
import com.apirest.backend.dto.ActualizarPerfilDTO;
import com.apirest.backend.dto.CambiarContrasenaDTO;
import com.apirest.backend.dto.LoginDTO;
import com.apirest.backend.dto.LoginRespuestaDTO;
import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroProfesorDTO;
import com.apirest.backend.exception.CredencialesInvalidasException;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Profesor;
import com.apirest.backend.model.Usuario;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IProfesorRepository;
import com.apirest.backend.repository.IUsuarioRepository;
import com.apirest.backend.security.JwtService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImpTest {

    @Mock IUsuarioRepository usuarioRepository;
    @Mock IEstudianteRepository estudianteRepository;
    @Mock IProfesorRepository profesorRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @InjectMocks
    UsuarioServiceImp servicio;

    private RegistroEstudianteDTO registro(String correo, String clave, String confirmacion) {
        RegistroEstudianteDTO dto = new RegistroEstudianteDTO();
        dto.setNombre("  Ana Pérez ");
        dto.setCorreo(correo);
        dto.setTelefono("3001234567");
        dto.setContrasena(clave);
        dto.setConfirmarContrasena(confirmacion);
        return dto;
    }

    private Usuario usuario(String correo, Rol rol, String hash) {
        Usuario u = new Usuario();
        u.setIdUsuario(7);
        u.setCorreo(correo);
        u.setNombre("Ana");
        u.setRol(rol);
        u.setContrasena(hash);
        return u;
    }

    private CambiarContrasenaDTO cambio(String actual, String nueva, String confirmacion) {
        CambiarContrasenaDTO dto = new CambiarContrasenaDTO();
        dto.setContrasenaActual(actual);
        dto.setContrasenaNueva(nueva);
        dto.setConfirmarContrasena(confirmacion);
        return dto;
    }

    // ---------- Registro ----------

    @Test
    void registraSiempreComoEstudianteConCorreoNormalizadoYClaveCifrada() {
        when(passwordEncoder.encode("ClaveSegura1")).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(estudianteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = servicio.registrarEstudiante(registro("  Ana@Correo.COM ", "ClaveSegura1", "ClaveSegura1"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertEquals("ana@correo.com", guardado.getCorreo());
        assertEquals("Ana Pérez", guardado.getNombre());
        assertEquals("HASH", guardado.getContrasena());   // nunca la clave en texto plano
        assertEquals(Rol.ESTUDIANTE, guardado.getRol());  // el registro público no puede crear admins
        assertEquals(Rol.ESTUDIANTE, respuesta.getRol());
    }

    @Test
    void unaTarjetaDeIdentidadEnBlancoSeGuardaComoNula() {
        when(passwordEncoder.encode(anyString())).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(estudianteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RegistroEstudianteDTO dto = registro("ana@correo.com", "ClaveSegura1", "ClaveSegura1");
        dto.setTarjetaIdentidad("   ");

        servicio.registrarEstudiante(dto);

        // Si se guardara "" chocaría con la restricción UNIQUE de otros estudiantes sin tarjeta
        ArgumentCaptor<Estudiante> captor = ArgumentCaptor.forClass(Estudiante.class);
        verify(estudianteRepository).save(captor.capture());
        assertNull(captor.getValue().getTarjetaIdentidad());
        verify(estudianteRepository, never()).existsByTarjetaIdentidad(anyString());
    }

    @Test
    void noRegistraUnCorreoRepetido() {
        when(usuarioRepository.existsByCorreo("ana@correo.com")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class,
                () -> servicio.registrarEstudiante(registro("ANA@correo.com", "ClaveSegura1", "ClaveSegura1")));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void noRegistraSiLasContrasenasNoCoinciden() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(registro("ana@correo.com", "ClaveSegura1", "OtraClave1")));

        verify(usuarioRepository, never()).save(any());
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void noRegistraUnEstudianteConContrasenaDeMasDe72Bytes() {
        // 40 "ñ" = 80 bytes: pasa el @Size(max = 72) del DTO, pero BCrypt la rechazaría con un mensaje en inglés
        String larga = "ñ".repeat(40);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(registro("ana@correo.com", larga, larga)));

        assertEquals("La contraseña no puede superar los 72 bytes (las tildes y la ñ cuentan como 2).", error.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void noRegistraUnProfesorConContrasenaDeMasDe72Bytes() {
        String larga = "ñ".repeat(40);
        RegistroProfesorDTO dto = new RegistroProfesorDTO();
        dto.setNombre("Laura Gómez");
        dto.setCorreo("laura@correo.com");
        dto.setContrasena(larga);
        dto.setConfirmarContrasena(larga);

        assertThrows(IllegalArgumentException.class, () -> servicio.registrarProfesor(dto));

        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void noRegistraUnaTarjetaDeIdentidadRepetida() {
        when(estudianteRepository.existsByTarjetaIdentidad("1098765432")).thenReturn(true);
        RegistroEstudianteDTO dto = registro("ana@correo.com", "ClaveSegura1", "ClaveSegura1");
        dto.setTarjetaIdentidad(" 1098765432 ");

        assertThrows(RecursoDuplicadoException.class, () -> servicio.registrarEstudiante(dto));

        verify(usuarioRepository, never()).save(any());
    }

    // ---------- Inicio de sesión ----------

    @Test
    void iniciaSesionConElCorreoEnCualquierMayusculaYDevuelveToken() {
        Usuario ana = usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH");
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(ana));
        when(passwordEncoder.matches("ClaveSegura1", "HASH")).thenReturn(true);
        when(jwtService.generarToken(ana)).thenReturn("jwt");

        LoginRespuestaDTO respuesta = servicio.iniciarSesion(new LoginDTO(" ANA@correo.com ", "ClaveSegura1"));

        assertEquals("jwt", respuesta.getToken());
        assertEquals(Rol.ESTUDIANTE, respuesta.getUsuario().getRol());
    }

    @Test
    void unaContrasenaErradaNoDaToken() {
        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH")));
        when(passwordEncoder.matches("Equivocada", "HASH")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion(new LoginDTO("ana@correo.com", "Equivocada")));

        verify(jwtService, never()).generarToken(any());
    }

    @Test
    void unCorreoInexistenteFallaIgualQueUnaContrasenaErrada() {
        when(usuarioRepository.findByCorreo("nadie@correo.com")).thenReturn(Optional.empty());

        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion(new LoginDTO("nadie@correo.com", "ClaveSegura1")));

        // Mismo mensaje en ambos casos: no permite averiguar qué correos están registrados
        assertEquals("Correo o contraseña incorrectos.", error.getMessage());
    }

    // ---------- Perfil ----------

    @Test
    void actualizarElNombreDeUnEstudianteLoCambiaTambienEnSuPerfilDeEstudiante() {
        Usuario ana = usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH");
        ana.setTelefono("3001234567");
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre("Ana");
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(ana));
        when(estudianteRepository.findByIdUsuario_Correo("ana@correo.com")).thenReturn(Optional.of(estudiante));
        ActualizarPerfilDTO datos = new ActualizarPerfilDTO();
        datos.setNombre("  Ana María ");

        servicio.actualizarPerfil("ana@correo.com", datos);

        assertEquals("Ana María", ana.getNombre());
        assertEquals("Ana María", estudiante.getNombre());
        assertEquals("3001234567", ana.getTelefono()); // sin teléfono en la petición, se conserva
        verify(estudianteRepository).save(estudiante);
    }

    @Test
    void actualizarElTelefonoDeUnProfesorLoSincronizaEnAmbasTablas() {
        Usuario laura = usuario("laura@correo.com", Rol.PROFESOR, "HASH");
        Profesor profesor = new Profesor();
        when(usuarioRepository.findByCorreo("laura@correo.com")).thenReturn(Optional.of(laura));
        when(profesorRepository.findByIdUsuario_Correo("laura@correo.com")).thenReturn(Optional.of(profesor));
        ActualizarPerfilDTO datos = new ActualizarPerfilDTO();
        datos.setNombre("Laura Gómez");
        datos.setTelefono("3109876543");

        servicio.actualizarPerfil("laura@correo.com", datos);

        assertEquals("3109876543", laura.getTelefono());
        assertEquals("3109876543", profesor.getTelefono());
        assertEquals("Laura Gómez", profesor.getNombre());
    }

    // ---------- Cambio de contraseña ----------

    @Test
    void cambiaLaContrasenaCifrandoLaNueva() {
        Usuario ana = usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH_VIEJO");
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(ana));
        when(passwordEncoder.matches("ClaveVieja1", "HASH_VIEJO")).thenReturn(true);
        when(passwordEncoder.encode("ClaveNueva1")).thenReturn("HASH_NUEVO");

        servicio.cambiarContrasena("ana@correo.com", cambio("ClaveVieja1", "ClaveNueva1", "ClaveNueva1"));

        assertEquals("HASH_NUEVO", ana.getContrasena());
        verify(usuarioRepository).save(ana);
    }

    @Test
    void noCambiaLaContrasenaSiLaActualEsIncorrecta() {
        Usuario ana = usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH_VIEJO");
        when(usuarioRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(ana));
        when(passwordEncoder.matches("Equivocada", "HASH_VIEJO")).thenReturn(false);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.cambiarContrasena("ana@correo.com", cambio("Equivocada", "ClaveNueva1", "ClaveNueva1")));

        assertEquals("La contraseña actual no es correcta.", error.getMessage());
        assertSame("HASH_VIEJO", ana.getContrasena());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void noCambiaLaContrasenaSiLaConfirmacionNoCoincide() {
        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH")));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.cambiarContrasena("ana@correo.com", cambio("ClaveVieja1", "ClaveNueva1", "Distinta1")));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rechazaUnaContrasenaNuevaDeMasDe72BytesConMensajeEnEspanol() {
        when(usuarioRepository.findByCorreo("ana@correo.com"))
                .thenReturn(Optional.of(usuario("ana@correo.com", Rol.ESTUDIANTE, "HASH")));
        // 40 caracteres "ñ" = 80 bytes en UTF-8: pasa el @Size(max = 72) pero BCrypt no la admite
        String larga = "ñ".repeat(40);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.cambiarContrasena("ana@correo.com", cambio("ClaveVieja1", larga, larga)));

        assertEquals("La contraseña no puede superar los 72 bytes (las tildes y la ñ cuentan como 2).", error.getMessage());
    }
}
