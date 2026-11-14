package com.apirest.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.apirest.backend.Enums.EstadoPago;
import com.apirest.backend.dto.CrearPagoDTO;
import com.apirest.backend.dto.EstadoPagoDTO;
import com.apirest.backend.dto.PagoRespuestaDTO;
import com.apirest.backend.exception.RecursoDuplicadoException;
import com.apirest.backend.exception.RecursoNoEncontradoException;
import com.apirest.backend.model.Archivo;
import com.apirest.backend.model.Estudiante;
import com.apirest.backend.model.Pago;
import com.apirest.backend.repository.IEstudianteRepository;
import com.apirest.backend.repository.IPagoRepository;

@ExtendWith(MockitoExtension.class)
class PagoServiceImpTest {

    private static final String CORREO = "ana@correo.com";

    @Mock
    IPagoRepository pagoRepository;

    @Mock
    IEstudianteRepository estudianteRepository;

    @Mock
    IArchivoService archivoService;

    @InjectMocks
    PagoServiceImp pagoService;

    private Estudiante estudiante;
    private Archivo comprobante;
    private MockMultipartFile pdf;

    @BeforeEach
    void preparar() {
        estudiante = new Estudiante();
        estudiante.setIdEstudiante(5);
        estudiante.setNombre("Ana Pérez");

        comprobante = new Archivo();
        comprobante.setIdArchivo(20);
        comprobante.setNombreOriginal("recibo.pdf");

        pdf = new MockMultipartFile("archivo", "recibo.pdf", "application/pdf", "%PDF-1.4".getBytes());
    }

    private CrearPagoDTO datos(String periodo) {
        CrearPagoDTO d = new CrearPagoDTO();
        d.setMonto(new BigDecimal("150000.00"));
        d.setPeriodo(periodo);
        d.setArchivo(pdf);
        return d;
    }

    private Pago pago(EstadoPago estado) {
        Pago p = new Pago();
        p.setIdPago(1);
        p.setIdEstudiante(estudiante);
        p.setIdArchivo(comprobante);
        p.setMonto(new BigDecimal("150000.00"));
        p.setPeriodo("2026-11");
        p.setEstado(estado);
        return p;
    }

    // ---------- crearPago ----------

    @Test
    void crearPagoNaceEnPendienteConFechaDeHoyYMesActual() {
        when(estudianteRepository.findByCorreoForUpdate(CORREO)).thenReturn(Optional.of(estudiante));
        String mesActual = YearMonth.now().toString();
        when(pagoRepository.existsByIdEstudiante_IdEstudianteAndPeriodoAndEstadoIn(eq(5), eq(mesActual), any()))
                .thenReturn(false);
        when(archivoService.registrar(CORREO, pdf, "comprobantes")).thenReturn(comprobante);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoRespuestaDTO respuesta = pagoService.crearPago(CORREO, datos(null));

        assertEquals(EstadoPago.PENDIENTE, respuesta.getEstado());
        assertEquals(LocalDate.now(), respuesta.getFechaPago());
        assertEquals(mesActual, respuesta.getPeriodo());
        assertEquals(new BigDecimal("150000.00"), respuesta.getMonto());
        assertEquals(20, respuesta.getIdArchivo());
        assertEquals("recibo.pdf", respuesta.getNombreArchivo());
        assertEquals("Ana Pérez", respuesta.getNombreEstudiante());
        assertNull(respuesta.getObservacion());
    }

    @Test
    void crearPagoUsaElPeriodoIndicado() {
        when(estudianteRepository.findByCorreoForUpdate(CORREO)).thenReturn(Optional.of(estudiante));
        when(pagoRepository.existsByIdEstudiante_IdEstudianteAndPeriodoAndEstadoIn(eq(5), eq("2026-03"), any()))
                .thenReturn(false);
        when(archivoService.registrar(CORREO, pdf, "comprobantes")).thenReturn(comprobante);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoRespuestaDTO respuesta = pagoService.crearPago(CORREO, datos("2026-03"));

        assertEquals("2026-03", respuesta.getPeriodo());
    }

    @Test
    void crearPagoRechazaUnSegundoPagoVigenteDelMismoMesSinGuardarElArchivo() {
        when(estudianteRepository.findByCorreoForUpdate(CORREO)).thenReturn(Optional.of(estudiante));
        when(pagoRepository.existsByIdEstudiante_IdEstudianteAndPeriodoAndEstadoIn(eq(5), eq("2026-11"), any()))
                .thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> pagoService.crearPago(CORREO, datos("2026-11")));

        verifyNoInteractions(archivoService);
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void crearPagoFallaSiNoHayPerfilDeEstudiante() {
        when(estudianteRepository.findByCorreoForUpdate(CORREO)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> pagoService.crearPago(CORREO, datos(null)));

        verifyNoInteractions(archivoService);
    }

    // ---------- consultas ----------

    @Test
    void listarMisPagosDevuelveSoloLosDelEstudiante() {
        when(estudianteRepository.findByIdUsuario_Correo(CORREO)).thenReturn(Optional.of(estudiante));
        when(pagoRepository.findByIdEstudiante_IdEstudianteOrderByFechaPagoDescIdPagoDesc(5))
                .thenReturn(List.of(pago(EstadoPago.PENDIENTE)));

        List<PagoRespuestaDTO> pagos = pagoService.listarMisPagos(CORREO);

        assertEquals(1, pagos.size());
        assertEquals(5, pagos.get(0).getIdEstudiante());
    }

    @Test
    void listarPagosPasaLosFiltrosAlRepositorio() {
        when(pagoRepository.buscar(EstadoPago.PENDIENTE, "2026-11"))
                .thenReturn(List.of(pago(EstadoPago.PENDIENTE)));

        List<PagoRespuestaDTO> pagos = pagoService.listarPagos(EstadoPago.PENDIENTE, "2026-11");

        assertEquals(1, pagos.size());
    }

    @Test
    void listarPagosSinFiltrosNoFiltra() {
        when(pagoRepository.buscar(null, null)).thenReturn(List.of());

        assertEquals(0, pagoService.listarPagos(null, "  ").size()); // un periodo en blanco equivale a sin filtro
    }

    @Test
    void listarPagosRechazaUnPeriodoMalFormado() {
        assertThrows(IllegalArgumentException.class, () -> pagoService.listarPagos(null, "2026-13"));
        assertThrows(IllegalArgumentException.class, () -> pagoService.listarPagos(null, "noviembre"));

        verifyNoInteractions(pagoRepository);
    }

    // ---------- actualizarEstado ----------

    @Test
    void apruebaUnPagoPendiente() {
        when(pagoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(pago(EstadoPago.PENDIENTE)));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        EstadoPagoDTO cambio = new EstadoPagoDTO();
        cambio.setEstado(EstadoPago.APROBADO);

        assertEquals(EstadoPago.APROBADO, pagoService.actualizarEstado(1, cambio).getEstado());
    }

    @Test
    void rechazaUnPagoConObservacion() {
        when(pagoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(pago(EstadoPago.PENDIENTE)));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        EstadoPagoDTO cambio = new EstadoPagoDTO();
        cambio.setEstado(EstadoPago.RECHAZADO);
        cambio.setObservacion("  Comprobante borroso  ");

        PagoRespuestaDTO respuesta = pagoService.actualizarEstado(1, cambio);

        assertEquals(EstadoPago.RECHAZADO, respuesta.getEstado());
        assertEquals("Comprobante borroso", respuesta.getObservacion());
    }

    @Test
    void rechazarSinObservacionEsInvalido() {
        when(pagoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(pago(EstadoPago.PENDIENTE)));

        EstadoPagoDTO cambio = new EstadoPagoDTO();
        cambio.setEstado(EstadoPago.RECHAZADO);
        cambio.setObservacion("   ");

        assertThrows(IllegalArgumentException.class, () -> pagoService.actualizarEstado(1, cambio));

        verify(pagoRepository, never()).save(any());
    }

    @Test
    void unPagoYaResueltoNoSePuedeModificar() {
        when(pagoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(pago(EstadoPago.APROBADO)));

        EstadoPagoDTO cambio = new EstadoPagoDTO();
        cambio.setEstado(EstadoPago.RECHAZADO);
        cambio.setObservacion("error");

        assertThrows(IllegalArgumentException.class, () -> pagoService.actualizarEstado(1, cambio));
    }

    @Test
    void noSePuedeVolverAPendiente() {
        when(pagoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(pago(EstadoPago.PENDIENTE)));

        EstadoPagoDTO cambio = new EstadoPagoDTO();
        cambio.setEstado(EstadoPago.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> pagoService.actualizarEstado(1, cambio));
    }

    @Test
    void actualizarUnPagoInexistenteDaNoEncontrado() {
        when(pagoRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());

        EstadoPagoDTO cambio = new EstadoPagoDTO();
        cambio.setEstado(EstadoPago.APROBADO);

        assertThrows(RecursoNoEncontradoException.class, () -> pagoService.actualizarEstado(99, cambio));
    }
}
