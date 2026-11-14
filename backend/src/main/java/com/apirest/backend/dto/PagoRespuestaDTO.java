package com.apirest.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.apirest.backend.Enums.EstadoPago;

import lombok.AllArgsConstructor;
import lombok.Data;

// Salida de la API de pagos. El comprobante se pide aparte con GET /api/archivos/{idArchivo}
@Data
@AllArgsConstructor
public class PagoRespuestaDTO {

    private Integer idPago;
    private Integer idEstudiante;
    private String nombreEstudiante;
    private BigDecimal monto;
    private String periodo;
    private LocalDate fechaPago;
    private EstadoPago estado;
    private String observacion;
    private Integer idArchivo;
    private String nombreArchivo;
}
