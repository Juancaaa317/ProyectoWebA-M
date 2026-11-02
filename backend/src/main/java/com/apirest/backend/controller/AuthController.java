package com.apirest.backend.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.RegistroEstudianteDTO;
import com.apirest.backend.dto.RegistroEstudianteRespuestaDTO;
import com.apirest.backend.dto.LoginDTO;
import com.apirest.backend.dto.LoginRespuestaDTO;
import com.apirest.backend.service.IUsuarioService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    IUsuarioService usuarioService;

    // Registrar un nuevo estudiante
    @PostMapping("/register")
    ResponseEntity<RegistroEstudianteRespuestaDTO> registrarEstudiante(@Valid @RequestBody RegistroEstudianteDTO registro) {
        RegistroEstudianteRespuestaDTO respuesta=usuarioService.registrarEstudiante(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/login")
    ResponseEntity<LoginRespuestaDTO> iniciarSesion(@Valid @RequestBody LoginDTO login) {
        return ResponseEntity.ok(usuarioService.iniciarSesion(login));
    }
}
