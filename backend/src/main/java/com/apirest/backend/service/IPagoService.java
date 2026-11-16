package com.apirest.backend.service;

import java.util.List;

import com.apirest.backend.Enums.EstadoPago;
import com.apirest.backend.dto.CrearPagoDTO;
import com.apirest.backend.dto.EstadoPagoDTO;
import com.apirest.backend.dto.PagoRespuestaDTO;

public interface IPagoService {

    PagoRespuestaDTO crearPago(String correo, CrearPagoDTO datos);

    List<PagoRespuestaDTO> listarMisPagos(String correo);

    // Filtros opcionales: estado y periodo (AAAA-MM). Con ambos en null devuelve todos
    List<PagoRespuestaDTO> listarPagos(EstadoPago estado, String periodo);

    PagoRespuestaDTO actualizarEstado(Integer idPago, EstadoPagoDTO datos);
}
