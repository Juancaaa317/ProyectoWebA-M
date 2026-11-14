package com.apirest.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.apirest.backend.Enums.EstadoPago;
import com.apirest.backend.dto.CrearPagoDTO;
import com.apirest.backend.dto.EstadoPagoDTO;
import com.apirest.backend.dto.PagoRespuestaDTO;
import com.apirest.backend.exception.GlobalExceptionHandler;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.TipoArchivoNoPermitidoException;
import com.apirest.backend.service.IPagoService;

// MockMvc "standalone": prueba validación y códigos HTTP sin levantar Spring ni la BD.
// Los permisos por rol viven en SecurityConfig y se verifican con pruebas en vivo.
@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    private static final String CORREO = "ana@correo.com";

    @Mock
    IPagoService pagoService;

    @InjectMocks
    PagoController controller;

    MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(CORREO)
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

    private MockMultipartFile comprobante() {
        return new MockMultipartFile("archivo", "recibo.pdf", "application/pdf", "%PDF-1.4".getBytes());
    }

    private PagoRespuestaDTO respuesta(EstadoPago estado) {
        return new PagoRespuestaDTO(1, 5, "Ana Pérez", new BigDecimal("150000.00"), "2026-11",
                LocalDate.of(2026, 11, 12), estado, null, 20, "recibo.pdf");
    }

    // ---------- POST /api/pagos ----------

    @Test
    void creaUnPagoConArchivoYMonto() throws Exception {
        when(pagoService.crearPago(eq(CORREO), any(CrearPagoDTO.class))).thenReturn(respuesta(EstadoPago.PENDIENTE));

        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "150000.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.idArchivo").value(20));

        ArgumentCaptor<CrearPagoDTO> captor = ArgumentCaptor.forClass(CrearPagoDTO.class);
        verify(pagoService).crearPago(eq(CORREO), captor.capture());
        assertEquals(new BigDecimal("150000.00"), captor.getValue().getMonto());
        assertNotNull(captor.getValue().getArchivo());
        assertEquals("recibo.pdf", captor.getValue().getArchivo().getOriginalFilename());
    }

    @Test
    void exigeElMonto() throws Exception {
        mockMvc.perform(multipart("/api/pagos").file(comprobante()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagoService);
    }

    @Test
    void rechazaUnMontoCeroONegativo() throws Exception {
        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "-5"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagoService);
    }

    @Test
    void rechazaUnMontoConMasDeDosDecimales() throws Exception {
        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "10.999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaUnMontoQueNoEsNumero() throws Exception {
        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagoService);
    }

    @Test
    void rechazaUnPeriodoConFormatoIncorrecto() throws Exception {
        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "100").param("periodo", "2026-13"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "100").param("periodo", "noviembre"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagoService);
    }

    @Test
    void respondeConflictoSiYaHayUnPagoDelMes() throws Exception {
        when(pagoService.crearPago(eq(CORREO), any(CrearPagoDTO.class)))
                .thenThrow(new RecursoDuplicadoException("Ya existe un pago pendiente o aprobado para el periodo 2026-11."));

        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "100"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void respondeUnsupportedMediaTypeSiElArchivoNoEsPermitido() throws Exception {
        when(pagoService.crearPago(eq(CORREO), any(CrearPagoDTO.class)))
                .thenThrow(new TipoArchivoNoPermitidoException("Tipo de archivo no permitido."));

        mockMvc.perform(multipart("/api/pagos").file(comprobante()).param("monto", "100"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));
    }

    // ---------- GET ----------

    @Test
    void listaMisPagos() throws Exception {
        when(pagoService.listarMisPagos(CORREO)).thenReturn(List.of(respuesta(EstadoPago.PENDIENTE)));

        mockMvc.perform(get("/api/pagos/mios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].periodo").value("2026-11"))
                .andExpect(jsonPath("$[0].fechaPago").value("2026-11-12"));
    }

    @Test
    void listaTodosLosPagos() throws Exception {
        when(pagoService.listarPagos(null, null)).thenReturn(List.of(respuesta(EstadoPago.APROBADO)));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("APROBADO"));
    }

    @Test
    void filtraLosPagosPorEstadoYPeriodo() throws Exception {
        when(pagoService.listarPagos(EstadoPago.PENDIENTE, "2026-11"))
                .thenReturn(List.of(respuesta(EstadoPago.PENDIENTE)));

        mockMvc.perform(get("/api/pagos").param("estado", "PENDIENTE").param("periodo", "2026-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void rechazaUnFiltroDeEstadoDesconocido() throws Exception {
        mockMvc.perform(get("/api/pagos").param("estado", "INVENTADO"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagoService);
    }

    @Test
    void periodoInvalidoEnElFiltroDaBadRequest() throws Exception {
        when(pagoService.listarPagos(null, "2026-13"))
                .thenThrow(new IllegalArgumentException("El periodo debe tener el formato AAAA-MM."));

        mockMvc.perform(get("/api/pagos").param("periodo", "2026-13"))
                .andExpect(status().isBadRequest());
    }

    // ---------- PATCH /api/pagos/{id}/estado ----------

    @Test
    void cambiaElEstadoDeUnPago() throws Exception {
        when(pagoService.actualizarEstado(eq(1), any(EstadoPagoDTO.class))).thenReturn(respuesta(EstadoPago.APROBADO));

        mockMvc.perform(patch("/api/pagos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void exigeElEstadoAlCambiarlo() throws Exception {
        mockMvc.perform(patch("/api/pagos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pagoService);
    }

    @Test
    void rechazaUnEstadoDesconocido() throws Exception {
        mockMvc.perform(patch("/api/pagos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"INVENTADO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transicionNoPermitidaDaBadRequest() throws Exception {
        when(pagoService.actualizarEstado(eq(1), any(EstadoPagoDTO.class)))
                .thenThrow(new IllegalArgumentException("Solo se puede cambiar el estado de un pago pendiente."));

        mockMvc.perform(patch("/api/pagos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
