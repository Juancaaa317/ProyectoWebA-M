package com.apirest.backend.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.dto.RegistroProfesorDTO;
import com.apirest.backend.dto.RegistroProfesorRespuestaDTO;
import com.apirest.backend.service.IUsuarioService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    IUsuarioService usuarioService;

    @PostMapping("/profesores")
    public ResponseEntity<RegistroProfesorRespuestaDTO> registrarProfesor(
            @Valid @RequestBody RegistroProfesorDTO registro) {

        RegistroProfesorRespuestaDTO respuesta =
                usuarioService.registrarProfesor(registro);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
