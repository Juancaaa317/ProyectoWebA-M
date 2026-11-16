package com.apirest.backend.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.apirest.backend.dto.ArchivoDescargaDTO;
import com.apirest.backend.exception.GlobalExceptionHandler;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.service.IArchivoService;

// MockMvc "standalone": prueba el controlador y el manejo de errores sin levantar Spring ni la BD
@ExtendWith(MockitoExtension.class)
class ArchivoControllerTest {

    @Mock
    IArchivoService archivoService;

    @InjectMocks
    ArchivoController controller;

    MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("ana@correo.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private ArchivoDescargaDTO pdf() {
        return new ArchivoDescargaDTO(
                new ByteArrayResource("%PDF-1.4".getBytes()), "recibo octubre.pdf", "application/pdf");
    }

    @Test
    void muestraElArchivoEnLineaPorDefecto() throws Exception {
        when(archivoService.obtenerParaDescarga("ana@correo.com", 10)).thenReturn(pdf());

        mockMvc.perform(get("/api/archivos/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes("%PDF-1.4".getBytes()))
                .andExpect(result -> {
                    String disposicion = result.getResponse().getHeader("Content-Disposition");
                    assertTrue(disposicion.startsWith("inline"), disposicion);
                    assertTrue(disposicion.contains("recibo"), disposicion);
                });
    }

    @Test
    void fuerzaLaDescargaConDescargarTrue() throws Exception {
        when(archivoService.obtenerParaDescarga("ana@correo.com", 10)).thenReturn(pdf());

        mockMvc.perform(get("/api/archivos/10").param("descargar", "true"))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(
                        result.getResponse().getHeader("Content-Disposition").startsWith("attachment")));
    }

    @Test
    void respondeNoEncontradoCuandoElArchivoNoExiste() throws Exception {
        when(archivoService.obtenerParaDescarga("ana@correo.com", 99))
                .thenThrow(new RecursoNoEncontradoException("No se encontró un archivo con el ID: 99"));

        mockMvc.perform(get("/api/archivos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/archivos/99"));
    }

    @Test
    void respondeProhibidoCuandoNoEsElDueno() throws Exception {
        when(archivoService.obtenerParaDescarga("ana@correo.com", 10))
                .thenThrow(new AccessDeniedException("no"));

        mockMvc.perform(get("/api/archivos/10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void rechazaUnIdQueNoEsNumerico() throws Exception {
        mockMvc.perform(get("/api/archivos/abc"))
                .andExpect(status().isBadRequest());
    }
}
