package com.apirest.backend.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entrada de POST /api/pagos (multipart/form-data). El estado y la fecha NO se reciben: los fija el servidor.
@Data
@NoArgsConstructor
public class CrearPagoDTO {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El monto admite hasta 2 decimales")
    private BigDecimal monto;

    // Opcional: si no se envía, se usa el mes actual
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "El periodo debe tener el formato AAAA-MM")
    private String periodo;

    // Comprobante (pdf, png o jpg). Su validación de tipo y tamaño la hace el almacenamiento
    private MultipartFile archivo;
}
