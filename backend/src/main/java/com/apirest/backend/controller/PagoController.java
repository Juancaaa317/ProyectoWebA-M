package com.apirest.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.Enums.EstadoPago;
import com.apirest.backend.dto.ApiErrorDTO;
import com.apirest.backend.dto.CrearPagoDTO;
import com.apirest.backend.dto.EstadoPagoDTO;
import com.apirest.backend.dto.PagoRespuestaDTO;
import com.apirest.backend.service.IPagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos")
public class PagoController {

    @Autowired
    IPagoService pagoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Registrar un pago mensual", description = "El estudiante (o su acudiente desde su cuenta) sube el comprobante y el monto. El pago nace en estado PENDIENTE. Campos multipart: monto, archivo (pdf, png o jpg, máx. 5 MB) y periodo opcional AAAA-MM (por defecto, el mes actual). Requiere rol ESTUDIANTE.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago registrado como PENDIENTE."),
            @ApiResponse(responseCode = "400", description = "Datos o archivo inválidos.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ESTUDIANTE."),
            @ApiResponse(responseCode = "404", description = "No se encontró el perfil de estudiante.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un pago pendiente o aprobado para ese periodo.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "413", description = "El archivo supera el tamaño máximo.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "415", description = "Tipo de archivo no permitido.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<PagoRespuestaDTO> crearPago(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @ModelAttribute CrearPagoDTO datos) {

        PagoRespuestaDTO respuesta = pagoService.crearPago(jwt.getSubject(), datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/mios")
    @Operation(summary = "Listar mis pagos", description = "Devuelve los pagos del estudiante autenticado, del más reciente al más antiguo. Requiere rol ESTUDIANTE.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos propios."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ESTUDIANTE."),
            @ApiResponse(responseCode = "404", description = "No se encontró el perfil de estudiante.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<List<PagoRespuestaDTO>> listarMisPagos(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pagoService.listarMisPagos(jwt.getSubject()));
    }

    @GetMapping
    @Operation(summary = "Listar los pagos", description = "Consulta los pagos de todos los estudiantes. Filtros opcionales: estado (PENDIENTE, APROBADO, RECHAZADO) y periodo (AAAA-MM). Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos."),
            @ApiResponse(responseCode = "400", description = "Estado o periodo con valor no válido.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR.")
    })
    public ResponseEntity<List<PagoRespuestaDTO>> listarPagos(
            @Parameter(description = "Filtrar por estado.") @RequestParam(required = false) EstadoPago estado,
            @Parameter(description = "Filtrar por periodo, formato AAAA-MM.") @RequestParam(required = false) String periodo) {
        return ResponseEntity.ok(pagoService.listarPagos(estado, periodo));
    }

    @PatchMapping("/{idPago}/estado")
    @Operation(summary = "Aprobar o rechazar un pago", description = "Resuelve un pago PENDIENTE. Rechazar exige una observación con el motivo. Requiere rol ADMINISTRADOR.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado."),
            @ApiResponse(responseCode = "400", description = "Transición no permitida o falta la observación al rechazar.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMINISTRADOR."),
            @ApiResponse(responseCode = "404", description = "No existe el pago indicado.", content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<PagoRespuestaDTO> actualizarEstado(
            @Parameter(description = "Identificador del pago.") @PathVariable Integer idPago,
            @Valid @RequestBody EstadoPagoDTO datos) {
        return ResponseEntity.ok(pagoService.actualizarEstado(idPago, datos));
    }
}
