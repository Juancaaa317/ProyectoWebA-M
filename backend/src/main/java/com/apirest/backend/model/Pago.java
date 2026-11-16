package com.apirest.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.apirest.backend.Enums.EstadoPago;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Entity @AllArgsConstructor @NoArgsConstructor
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idPago;

    @ManyToOne
    @JoinColumn(name = "idEstudiante")
    protected Estudiante idEstudiante;

    // Comprobante de pago (metadatos del archivo guardado)
    @ManyToOne
    @JoinColumn(name = "idArchivo")
    protected Archivo idArchivo;

    protected BigDecimal monto;

    // Mes al que corresponde el pago, formato AAAA-MM
    protected String periodo;

    protected LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    protected EstadoPago estado;

    // Motivo escrito por el administrador (sobre todo al rechazar)
    protected String observacion;
}
