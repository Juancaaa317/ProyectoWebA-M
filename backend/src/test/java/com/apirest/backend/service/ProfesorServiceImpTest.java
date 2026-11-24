package com.apirest.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.apirest.backend.Enums.AreaDocencia;
import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.dto.ProfesorPublicoDTO;
import com.apirest.backend.model.Clase;
import com.apirest.backend.model.Profesor;
import com.apirest.backend.model.Programa;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IProfesorRepository;

@ExtendWith(MockitoExtension.class)
class ProfesorServiceImpTest {

    @Mock IProfesorRepository profesorRepository;
    @Mock IClaseRepository claseRepository;

    @InjectMocks
    ProfesorServiceImp servicio;

    private Profesor profesor(int id, String nombre) {
        Profesor p = new Profesor();
        p.setIdProfesor(id);
        p.setNombre(nombre);
        p.setAreaDocencia(AreaDocencia.DOCENTE);
        return p;
    }

    private Clase clase(Profesor profesor, String programa, EstadoPrograma estadoPrograma) {
        Programa pr = new Programa();
        pr.setNombrePrograma(programa);
        pr.setEstadoPrograma(estadoPrograma);
        Clase c = new Clase();
        c.setIdProfesor(profesor);
        c.setIdPrograma(pr);
        return c;
    }

    @Test
    void agrupaLasDisciplinasDeCadaProfesorSinRepetirYEnOrdenEspanol() {
        Profesor laura = profesor(1, "Laura Gómez");
        when(profesorRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(laura));
        when(claseRepository.findByEstadoClaseIn(any())).thenReturn(List.of(
                clase(laura, "Danza árabe", EstadoPrograma.ACTIVO),
                clase(laura, "Ballet", EstadoPrograma.ACTIVO),
                clase(laura, "Ballet", EstadoPrograma.ACTIVO),        // dos grupos del mismo programa
                clase(laura, "Árabe avanzado", EstadoPrograma.ACTIVO)));

        List<ProfesorPublicoDTO> resultado = servicio.listarPublicos();

        // "Árabe" con tilde va con la A, no después de la Z
        assertEquals(List.of("Árabe avanzado", "Ballet", "Danza árabe"), resultado.get(0).getDisciplinas());
    }

    @Test
    void noMuestraProgramasInactivos() {
        Profesor laura = profesor(1, "Laura Gómez");
        when(profesorRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(laura));
        when(claseRepository.findByEstadoClaseIn(any())).thenReturn(List.of(
                clase(laura, "Ballet", EstadoPrograma.ACTIVO),
                clase(laura, "Ritmos latinos", EstadoPrograma.INACTIVO)));

        assertEquals(List.of("Ballet"), servicio.listarPublicos().get(0).getDisciplinas());
    }

    @Test
    void unProfesorSinClasesAbiertasApareceConLaListaVacia() {
        when(profesorRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(profesor(2, "Andrés Restrepo")));
        when(claseRepository.findByEstadoClaseIn(any())).thenReturn(List.of());

        ProfesorPublicoDTO andres = servicio.listarPublicos().get(0);

        assertEquals("Andrés Restrepo", andres.getNombre());
        assertEquals(List.of(), andres.getDisciplinas());
    }

    @Test
    @SuppressWarnings("unchecked")
    void soloConsultaClasesEnCursoOProgramadas() {
        when(profesorRepository.findAllByOrderByNombreAsc()).thenReturn(List.of());
        when(claseRepository.findByEstadoClaseIn(any())).thenReturn(List.of());

        servicio.listarPublicos();

        ArgumentCaptor<Collection<EstadoClase>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(claseRepository).findByEstadoClaseIn(captor.capture());
        assertEquals(java.util.Set.of(EstadoClase.EN_CURSO, EstadoClase.PROGRAMADA), java.util.Set.copyOf(captor.getValue()));
    }
}
