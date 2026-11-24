package com.apirest.backend.service;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apirest.backend.Enums.EstadoClase;
import com.apirest.backend.Enums.EstadoPrograma;
import com.apirest.backend.dto.ProfesorPublicoDTO;
import com.apirest.backend.model.Clase;
import com.apirest.backend.repository.IClaseRepository;
import com.apirest.backend.repository.IProfesorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfesorServiceImp implements IProfesorService {

    // Clases que se muestran al público: las que están dictándose o por empezar
    private static final List<EstadoClase> CLASES_ABIERTAS =
            List.of(EstadoClase.EN_CURSO, EstadoClase.PROGRAMADA);

    // Orden alfabético en español: "Árabe" va con la A, no después de la Z
    private static final Collator ORDEN_ES = Collator.getInstance(Locale.forLanguageTag("es"));

    private final IProfesorRepository profesorRepository;
    private final IClaseRepository claseRepository;

    // Dos consultas en total, sin importar cuántos profesores haya: los profesores y las clases abiertas
    @Override
    @Transactional(readOnly = true)
    public List<ProfesorPublicoDTO> listarPublicos() {
        Map<Integer, TreeSet<String>> disciplinasPorProfesor = claseRepository.findByEstadoClaseIn(CLASES_ABIERTAS)
                .stream()
                .filter(clase -> clase.getIdPrograma().getEstadoPrograma() == EstadoPrograma.ACTIVO)
                .collect(Collectors.groupingBy(
                        clase -> clase.getIdProfesor().getIdProfesor(),
                        Collectors.mapping(this::nombrePrograma, Collectors.toCollection(() -> new TreeSet<>(ORDEN_ES)))));

        return profesorRepository.findAllByOrderByNombreAsc().stream()
                .map(profesor -> new ProfesorPublicoDTO(
                        profesor.getIdProfesor(),
                        profesor.getNombre(),
                        profesor.getAreaDocencia(),
                        List.copyOf(disciplinasPorProfesor.getOrDefault(profesor.getIdProfesor(), new TreeSet<>(ORDEN_ES)))))
                .toList();
    }

    private String nombrePrograma(Clase clase) {
        return clase.getIdPrograma().getNombrePrograma();
    }
}
