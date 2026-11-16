import { useCallback, useEffect, useState } from "react";
import { pagoService } from "../services/pagoService";
import ComprobanteAcciones from "../components/ComprobanteAcciones";
import { formatearMonto } from "../utils/formatoPago";
import "../styles/login.css";
import "../styles/adminProgramas.css";
import "../styles/pagos.css";

const TAMANO_MAXIMO = 5 * 1024 * 1024;
const TIPOS_PERMITIDOS = ["application/pdf", "image/png", "image/jpeg"];

function validar({ monto, archivo }) {
    if (!/^\d+([.,]\d{1,2})?$/.test(monto.trim()) || Number(monto.replace(",", ".")) <= 0) {
        return "Ingresa un monto mayor que cero con máximo 2 decimales.";
    }
    if (!archivo) return "Adjunta el comprobante de pago.";
    if (archivo.size === 0) return "El archivo está vacío.";
    if (!TIPOS_PERMITIDOS.includes(archivo.type)) {
        return "El comprobante debe ser un PDF, PNG o JPG.";
    }
    if (archivo.size > TAMANO_MAXIMO) return "El comprobante supera el máximo de 5 MB.";
    return "";
}

// Pantalla del ESTUDIANTE: enviar comprobantes y consultar su historial
function Pagos() {
    const [pagos, setPagos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState("");
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");
    const [enviando, setEnviando] = useState(false);
    const [monto, setMonto] = useState("");
    const [periodo, setPeriodo] = useState("");
    const [archivo, setArchivo] = useState(null);
    const [inputKey, setInputKey] = useState(0);

    const cargar = useCallback(async () => {
        setLoading(true);
        setLoadError("");
        try {
            setPagos(await pagoService.listarMios());
        } catch (err) {
            setLoadError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => { Promise.resolve().then(cargar); }, [cargar]);

    const enviar = async (event) => {
        event.preventDefault();
        setMensaje("");

        const problema = validar({ monto, archivo });
        if (problema) {
            setError(problema);
            return;
        }

        setEnviando(true);
        setError("");
        try {
            await pagoService.crear({ monto: monto.trim().replace(",", "."), periodo, archivo });
            setMensaje("Comprobante enviado. Queda pendiente de revisión.");
            setMonto("");
            setPeriodo("");
            setArchivo(null);
            setInputKey((k) => k + 1);
            await cargar();
        } catch (err) {
            setError(err.message);
        } finally {
            setEnviando(false);
        }
    };

    return (
        <main className="admin">
            <header className="admin-header">
                <div>
                    <p className="admin-subtitle">PANEL DE ESTUDIANTE</p>
                    <h1>Mis pagos</h1>
                </div>
            </header>

            {mensaje && <p className="admin-exito" role="status">{mensaje}</p>}
            {error && <p className="login-error" role="alert">{error}</p>}

            <form className="panel-form-inline pagos-form" onSubmit={enviar} noValidate>
                <label htmlFor="pago-monto">Monto
                    <input id="pago-monto" type="text" inputMode="decimal" value={monto}
                        onChange={(e) => setMonto(e.target.value)} placeholder="0.00" required />
                </label>
                <label htmlFor="pago-periodo">Periodo (opcional)
                    <input id="pago-periodo" type="month" value={periodo}
                        onChange={(e) => setPeriodo(e.target.value)} />
                    <span className="pagos-ayuda-campo">Si lo dejas vacío se usa el mes actual.</span>
                </label>
                <label htmlFor="pago-archivo">Comprobante
                    <input key={inputKey} id="pago-archivo" type="file"
                        accept=".pdf,.png,.jpg,.jpeg,application/pdf,image/png,image/jpeg"
                        onChange={(e) => setArchivo(e.target.files[0] ?? null)} required />
                    <span className="pagos-ayuda-campo">PDF, PNG o JPG · máximo 5 MB</span>
                </label>
                <button className="login-button" disabled={enviando}>
                    {enviando ? "Enviando..." : "Enviar comprobante"}
                </button>
            </form>

            {loadError && <p className="login-error" role="alert">{loadError}</p>}

            {loading ? <div className="admin-estado">Cargando pagos...</div> : (
                pagos.length === 0 ? (
                    !loadError && <p className="admin-vacio">Aún no has enviado comprobantes de pago.</p>
                ) : (
                    <div className="admin-tabla-wrapper">
                        <table className="admin-tabla">
                            <thead><tr>
                                <th>Periodo</th><th>Monto</th><th>Fecha de envío</th>
                                <th>Estado</th><th>Observación</th><th>Comprobante</th>
                            </tr></thead>
                            <tbody>{pagos.map((pago) => (
                                <tr key={pago.idPago}>
                                    <td>{pago.periodo}</td>
                                    <td>${formatearMonto(pago.monto)}</td>
                                    <td>{pago.fechaPago}</td>
                                    <td><span className={`admin-badge ${pago.estado.toLowerCase()}`}>{pago.estado}</span></td>
                                    <td className="pagos-observacion">{pago.observacion || "—"}</td>
                                    <td><ComprobanteAcciones pago={pago} onError={setError} /></td>
                                </tr>
                            ))}</tbody>
                        </table>
                    </div>
                )
            )}
        </main>
    );
}

export default Pagos;
