import { apiFetch, apiFetchBlob } from "./api";

export const pagoService = {
    listarMios: () => apiFetch("/pagos/mios"),

    // filtros opcionales: estado (PENDIENTE|APROBADO|RECHAZADO) y periodo (AAAA-MM)
    listarTodos: ({ estado = "", periodo = "" } = {}) => {
        const params = new URLSearchParams();
        if (estado) params.set("estado", estado);
        if (periodo) params.set("periodo", periodo);
        const consulta = params.toString();
        return apiFetch(`/pagos${consulta ? `?${consulta}` : ""}`);
    },

    // multipart/form-data: monto, periodo (opcional) y archivo
    crear: ({ monto, periodo, archivo }) => {
        const datos = new FormData();
        datos.append("monto", monto);
        if (periodo) datos.append("periodo", periodo);
        datos.append("archivo", archivo);
        return apiFetch("/pagos", { method: "POST", body: datos });
    },

    resolver: (idPago, estado, observacion) => apiFetch(`/pagos/${idPago}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estado, ...(observacion && { observacion }) }),
    }),

    // Blob del comprobante, pedido con el JWT en la cabecera
    obtenerComprobante: (idArchivo) => apiFetchBlob(`/archivos/${idArchivo}`),
};
