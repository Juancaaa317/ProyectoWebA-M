package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.CrearHorarioDTO;
import com.apirest.backend.dto.HorarioRespuestaDTO;
import com.apirest.backend.service.IHorarioService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    IHorarioService horarioService;

    @GetMapping
    public ResponseEntity<List<HorarioRespuestaDTO>> listarHorarios() {
        return ResponseEntity.ok(horarioService.listarHorarios());
    }

    @GetMapping("/clase/{idClase}")
    public ResponseEntity<List<HorarioRespuestaDTO>> listarPorClase(
            @PathVariable Integer idClase) {
        return ResponseEntity.ok(horarioService.listarPorClase(idClase));
    }

    @PostMapping
    public ResponseEntity<HorarioRespuestaDTO> crearHorario(
            @Valid @RequestBody CrearHorarioDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horarioService.crearHorario(datos));
    }

    @PutMapping("/{idHorario}")
    public ResponseEntity<HorarioRespuestaDTO> actualizarHorario(
            @PathVariable Integer idHorario,
            @Valid @RequestBody CrearHorarioDTO datos) {
        return ResponseEntity.ok(horarioService.actualizarHorario(idHorario, datos));
    }

    @DeleteMapping("/{idHorario}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Integer idHorario) {
        horarioService.eliminarHorario(idHorario);
        return ResponseEntity.noContent().build();
    }
}
