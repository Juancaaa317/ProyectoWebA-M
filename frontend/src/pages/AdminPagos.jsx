import { useCallback, useEffect, useState } from "react";
import { pagoService } from "../services/pagoService";
import ComprobanteAcciones from "../components/ComprobanteAcciones";
import { formatearMonto } from "../utils/formatoPago";
import "../styles/login.css";
import "../styles/adminProgramas.css";
import "../styles/pagos.css";

const ESTADOS = ["PENDIENTE", "APROBADO", "RECHAZADO"];

// Pantalla del ADMINISTRADOR: revisar comprobantes y aprobar o rechazar
function AdminPagos() {
    const [pagos, setPagos] = useState([]);
    const [estado, setEstado] = useState("");
    const [periodo, setPeriodo] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");
    const [savingId, setSavingId] = useState(null);
    const [rechazando, setRechazando] = useState(null);
    const [observacion, setObservacion] = useState("");
    const [errorModal, setErrorModal] = useState("");

    const cargar = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            setPagos(await pagoService.listarTodos({ estado, periodo }));
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [estado, periodo]);

    useEffect(() => { Promise.resolve().then(cargar); }, [cargar]);

    const aprobar = async (pago) => {
        setSavingId(pago.idPago);
        setError("");
        setMensaje("");
        try {
            await pagoService.resolver(pago.idPago, "APROBADO");
            setMensaje(`Pago de ${pago.nombreEstudiante} aprobado.`);
            await cargar();
        } catch (err) {
            setError(err.message);
        } finally {
            setSavingId(null);
        }
    };

    const abrirRechazo = (pago) => {
        setRechazando(pago);
        setObservacion("");
        setErrorModal("");
    };

    const rechazar = async (event) => {
        event.preventDefault();
        const motivo = observacion.trim();
        if (!motivo) {
            setErrorModal("Debes indicar el motivo del rechazo.");
            return;
        }

        setSavingId(rechazando.idPago);
        setErrorModal("");
        try {
            await pagoService.resolver(rechazando.idPago, "RECHAZADO", motivo);
            setMensaje(`Pago de ${rechazando.nombreEstudiante} rechazado.`);
            setRechazando(null);
            await cargar();
        } catch (err) {
            setErrorModal(err.message);
        } finally {
            setSavingId(null);
        }
    };

    return (
        <main className="admin">
            <header className="admin-header">
                <div>
                    <p className="admin-subtitle">PANEL DE ADMINISTRACIÓN</p>
                    <h1>Pagos</h1>
                </div>
            </header>

            {mensaje && <p className="admin-exito" role="status">{mensaje}</p>}
            {error && <p className="login-error" role="alert">{error}</p>}

            <div className="pagos-filtros">
                <label>Estado
                    <select value={estado} onChange={(e) => setEstado(e.target.value)}>
                        <option value="">Todos</option>
                        {ESTADOS.map((item) => <option key={item} value={item}>{item}</option>)}
                    </select>
                </label>
                <label>Periodo
                    <input type="month" value={periodo} onChange={(e) => setPeriodo(e.target.value)} />
                </label>
                {(estado || periodo) && (
                    <button type="button" className="admin-btn-cancelar"
                        onClick={() => { setEstado(""); setPeriodo(""); }}>
                        Limpiar filtros
                    </button>
                )}
            </div>

            {loading ? <div className="admin-estado">Cargando pagos...</div> : (
                pagos.length === 0 ? <p className="admin-vacio">No hay pagos para los filtros seleccionados.</p> :
                    <div className="admin-tabla-wrapper">
                        <table className="admin-tabla">
                            <thead><tr>
                                <th>Estudiante</th><th>Periodo</th><th>Monto</th><th>Fecha de envío</th>
                                <th>Estado</th><th>Observación</th><th>Comprobante</th><th>Acción</th>
                            </tr></thead>
                            <tbody>{pagos.map((pago) => (
                                <tr key={pago.idPago}>
                                    <td>{pago.nombreEstudiante}</td>
                                    <td>{pago.periodo}</td>
                                    <td>${formatearMonto(pago.monto)}</td>
                                    <td>{pago.fechaPago}</td>
                                    <td><span className={`admin-badge ${pago.estado.toLowerCase()}`}>{pago.estado}</span></td>
                                    <td className="pagos-observacion">{pago.observacion || "—"}</td>
                                    <td><ComprobanteAcciones pago={pago} onError={setError} /></td>
                                    <td>
                                        {pago.estado === "PENDIENTE" ? (
                                            <div className="admin-acciones">
                                                <button type="button" className="admin-btn-editar"
                                                    disabled={savingId === pago.idPago}
                                                    onClick={() => aprobar(pago)}>
                                                    {savingId === pago.idPago ? "Guardando..." : "Aprobar"}
                                                </button>
                                                <button type="button" className="admin-btn-eliminar"
                                                    disabled={savingId === pago.idPago}
                                                    onClick={() => abrirRechazo(pago)}>
                                                    Rechazar
                                                </button>
                                            </div>
                                        ) : <span className="pagos-ayuda-campo">Resuelto</span>}
                                    </td>
                                </tr>
                            ))}</tbody>
                        </table>
                    </div>
            )}

            {rechazando && (
                <div className="admin-modal-fondo" role="dialog" aria-modal="true" aria-labelledby="titulo-rechazo">
                    <form className="admin-modal admin-form" onSubmit={rechazar}>
                        <h2 id="titulo-rechazo">Rechazar pago</h2>
                        <p className="admin-confirmar-texto">
                            Pago de {rechazando.nombreEstudiante} · {rechazando.periodo} · ${formatearMonto(rechazando.monto)}.
                            Indica el motivo para que el estudiante lo vea.
                        </p>
                        {errorModal && <p className="login-error" role="alert">{errorModal}</p>}
                        <label htmlFor="pago-observacion">Observación
                            <textarea id="pago-observacion" rows={4} maxLength={255} value={observacion}
                                onChange={(e) => setObservacion(e.target.value)} autoFocus required />
                        </label>
                        <div className="admin-form-botones">
                            <button type="button" className="admin-btn-cancelar"
                                disabled={savingId !== null} onClick={() => setRechazando(null)}>
                                Cancelar
                            </button>
                            <button className="login-button" disabled={savingId !== null}>
                                {savingId !== null ? "Rechazando..." : "Confirmar rechazo"}
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </main>
    );
}

export default AdminPagos;
