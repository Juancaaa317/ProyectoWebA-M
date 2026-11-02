package com.apirest.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.apirest.backend.Enums.EstadoInscripcion;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Inscripcion;
import com.apirest.backend.model.Programa;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IInscripcionRepository;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceImpTest {

    private static final Integer ID_INSCRIPCION = 5;
    private static final Integer ID_CLASE = 8;
    private static final Integer CUPO_MAXIMO = 3;

    @Mock
    private IInscripcionRepository inscripcionRepository;

    @Mock
    private IClaseRepository claseRepository;

    @InjectMocks
    private InscripcionServiceImp inscripcionService;

    private Inscripcion inscripcion;
    private Clase clase;

    @BeforeEach
    void prepararInscripcionCancelada() {
        Programa programa = new Programa();
        programa.setNombrePrograma("Danza");

        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(12);
        estudiante.setNombre("Estudiante de prueba");

        clase = new Clase();
        clase.setIdClase(ID_CLASE);
        clase.setNombre("Ballet inicial");
        clase.setIdPrograma(programa);
        clase.setCupoMaximo(CUPO_MAXIMO);

        inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(ID_INSCRIPCION);
        inscripcion.setIdClase(clase);
        inscripcion.setIdEstudiante(estudiante);
        inscripcion.setFechaInscripcion(Date.valueOf("2026-11-02"));
        inscripcion.setEstadoInscripcion(EstadoInscripcion.CANCELADA);

        when(inscripcionRepository.findById(ID_INSCRIPCION))
                .thenReturn(Optional.of(inscripcion));
    }

    @Test
    void reabreInscripcionCanceladaComoPendienteCuandoHayCupo() {
        when(claseRepository.findByIdForUpdate(ID_CLASE))
                .thenReturn(Optional.of(clase));
        when(inscripcionRepository.countByIdClase_IdClaseAndEstadoInscripcionIn(
                ID_CLASE, List.of(EstadoInscripcion.PENDIENTE, EstadoInscripcion.ACTIVA)))
                .thenReturn(2L);
        when(inscripcionRepository.save(inscripcion)).thenReturn(inscripcion);

        var respuesta = inscripcionService.actualizarEstado(
                ID_INSCRIPCION, EstadoInscripcion.PENDIENTE);

        assertEquals(EstadoInscripcion.PENDIENTE, respuesta.getEstadoInscripcion());
        verify(claseRepository).findByIdForUpdate(ID_CLASE);
        verify(inscripcionRepository).save(inscripcion);
    }

    @Test
    void noReabreInscripcionCuandoLaClaseEstaLlena() {
        when(claseRepository.findByIdForUpdate(ID_CLASE))
                .thenReturn(Optional.of(clase));
        when(inscripcionRepository.countByIdClase_IdClaseAndEstadoInscripcionIn(
                ID_CLASE, List.of(EstadoInscripcion.PENDIENTE, EstadoInscripcion.ACTIVA)))
                .thenReturn((long) CUPO_MAXIMO);

        assertThrows(RecursoDuplicadoException.class,
                () -> inscripcionService.actualizarEstado(
                        ID_INSCRIPCION, EstadoInscripcion.PENDIENTE));

        verify(inscripcionRepository).findById(ID_INSCRIPCION);
    }

    @Test
    void noPermiteAprobarDirectamenteUnaInscripcionCancelada() {
        assertThrows(IllegalArgumentException.class,
                () -> inscripcionService.actualizarEstado(
                        ID_INSCRIPCION, EstadoInscripcion.ACTIVA));

        verifyNoInteractions(claseRepository);
    }
}
