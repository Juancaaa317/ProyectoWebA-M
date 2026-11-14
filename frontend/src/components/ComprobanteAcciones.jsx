import { useState } from "react";
import { pagoService } from "../services/pagoService";

const TIPOS_VISIBLES = ["application/pdf", "image/png", "image/jpeg"];

// Botones para ver o descargar el comprobante de un pago.
// El archivo se pide con fetch (JWT en cabecera) y se muestra desde un Blob local.
function ComprobanteAcciones({ pago, onError }) {
    const [accion, setAccion] = useState(null);

    if (!pago.idArchivo) return <span className="pagos-ayuda-campo">Sin comprobante</span>;

    const obtener = async (tipoAccion) => {
        setAccion(tipoAccion);
        onError("");

        // Se abre la pestaña de inmediato (gesto del usuario) para que el navegador no la bloquee
        const ventana = tipoAccion === "ver" ? window.open("", "_blank") : null;

        try {
            const blob = await pagoService.obtenerComprobante(pago.idArchivo);
            const url = URL.createObjectURL(blob);

            if (tipoAccion === "ver" && ventana && TIPOS_VISIBLES.includes(blob.type)) {
                ventana.location.href = url;
            } else {
                ventana?.close();
                const enlace = document.createElement("a");
                enlace.href = url;
                enlace.download = pago.nombreArchivo || `comprobante-${pago.idPago}`;
                document.body.appendChild(enlace);
                enlace.click();
                enlace.remove();
            }

            // La URL temporal se libera pasado un rato para no dejar el archivo en memoria
            setTimeout(() => URL.revokeObjectURL(url), 60_000);
        } catch (err) {
            ventana?.close();
            onError(err.message);
        } finally {
            setAccion(null);
        }
    };

    return (
        <div className="admin-acciones">
            <button type="button" className="admin-btn-editar" disabled={accion !== null}
                aria-label={`Ver comprobante del pago ${pago.idPago}`}
                onClick={() => obtener("ver")}>
                {accion === "ver" ? "Abriendo..." : "Ver"}
            </button>
            <button type="button" className="admin-btn-cancelar" disabled={accion !== null}
                aria-label={`Descargar comprobante del pago ${pago.idPago}`}
                onClick={() => obtener("descargar")}>
                {accion === "descargar" ? "Descargando..." : "Descargar"}
            </button>
        </div>
    );
}

export default ComprobanteAcciones;
