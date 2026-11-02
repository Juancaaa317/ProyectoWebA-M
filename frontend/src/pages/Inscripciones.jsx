import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ROLES } from "../config/roles";
import { claseService, inscripcionService } from "../services/panelService";
import "../styles/login.css";
import "../styles/adminProgramas.css";

const ESTADOS = ["PENDIENTE", "ACTIVA", "CANCELADA"];

function estadosPermitidos(estadoActual) {
    if (estadoActual === "PENDIENTE") return ESTADOS;
    if (estadoActual === "ACTIVA") return ["ACTIVA", "CANCELADA"];
    if (estadoActual === "CANCELADA") return ["CANCELADA", "PENDIENTE"];
    return [estadoActual];
}

function Inscripciones() {
    const { user } = useAuth();
    const esAdmin = user.rol === ROLES.ADMIN;
    const [inscripciones, setInscripciones] = useState([]);
    const [clases, setClases] = useState([]);
    const [claseId, setClaseId] = useState("");
    const [loading, setLoading] = useState(true);
    const [savingId, setSavingId] = useState(null);
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");

    const cargar = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const [lista, disponibles] = await Promise.all([
                esAdmin ? inscripcionService.listarTodas() : inscripcionService.listarMias(),
                esAdmin ? Promise.resolve([]) : claseService.listar(),
            ]);
            setInscripciones(lista);
            setClases(disponibles.filter((clase) =>
                clase.estadoClase === "PROGRAMADA" &&
                !lista.some((item) => item.idClase === clase.idClase && item.estadoInscripcion !== "CANCELADA")
            ));
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [esAdmin]);

    useEffect(() => { Promise.resolve().then(cargar); }, [cargar]);

    const inscribirse = async (event) => {
        event.preventDefault();
        if (!claseId) return;
        setSavingId("nueva");
        setError("");
        try {
            await inscripcionService.crear(Number(claseId));
            setMensaje("Solicitud de inscripción enviada.");
            setClaseId("");
            await cargar();
        } catch (err) {
            setError(err.message);
        } finally {
            setSavingId(null);
        }
    };

    const cambiarEstado = async (id, estado) => {
        setSavingId(id);
        setError("");
        try {
            await inscripcionService.cambiarEstado(id, estado);
            setMensaje("Estado de inscripción actualizado.");
            await cargar();
        } catch (err) {
            setError(err.message);
        } finally {
            setSavingId(null);
        }
    };

    return (
        <main className="admin">
            <header className="admin-header">
                <div>
                    <p className="admin-subtitle">{esAdmin ? "PANEL DE ADMINISTRACIÓN" : "PANEL DE ESTUDIANTE"}</p>
                    <h1>{esAdmin ? "Inscripciones" : "Mis inscripciones"}</h1>
                </div>
            </header>

            {mensaje && <p className="admin-exito" role="status">{mensaje}</p>}
            {error && <p className="login-error" role="alert">{error}</p>}

            {!esAdmin && (
                <form className="panel-form-inline" onSubmit={inscribirse}>
                    <label htmlFor="clase-inscripcion">Solicitar inscripción a una clase</label>
                    <select id="clase-inscripcion" value={claseId} onChange={(e) => setClaseId(e.target.value)} required>
                        <option value="">Selecciona una clase</option>
                        {clases.map((clase) => (
                            <option key={clase.idClase} value={clase.idClase}>
                                {clase.nombre} · {clase.nombrePrograma} · {clase.nombreProfesor}
                            </option>
                        ))}
                    </select>
                    <button className="login-button" disabled={savingId === "nueva"}>
                        {savingId === "nueva" ? "Enviando..." : "Solicitar inscripción"}
                    </button>
                </form>
            )}

            {loading ? <div className="admin-estado">Cargando inscripciones...</div> : (
                inscripciones.length === 0 ? <p className="admin-vacio">Aún no hay inscripciones para mostrar.</p> :
                    <div className="admin-tabla-wrapper">
                        <table className="admin-tabla">
                            <thead><tr>
                                {esAdmin && <th>Estudiante</th>}
                                <th>Clase</th><th>Programa</th><th>Fecha</th><th>Estado</th>
                                {esAdmin && <th>Acción</th>}
                            </tr></thead>
                            <tbody>{inscripciones.map((item) => (
                                <tr key={item.idInscripcion}>
                                    {esAdmin && <td>{item.nombreEstudiante}</td>}
                                    <td>{item.nombreClase}</td><td>{item.nombrePrograma}</td>
                                    <td>{item.fechaInscripcion}</td>
                                    <td><span className={`admin-badge ${item.estadoInscripcion.toLowerCase()}`}>{item.estadoInscripcion}</span></td>
                                    {esAdmin && <td>
                                        <select aria-label={`Cambiar estado de inscripción ${item.idInscripcion}`} value={item.estadoInscripcion}
                                            disabled={savingId === item.idInscripcion}
                                            onChange={(e) => cambiarEstado(item.idInscripcion, e.target.value)}>
                                            {estadosPermitidos(item.estadoInscripcion).map((estado) => <option key={estado} value={estado}>
                                                {item.estadoInscripcion === "CANCELADA" && estado === "PENDIENTE" ? "PENDIENTE (reabrir solicitud)" : estado}
                                            </option>)}
                                        </select>
                                    </td>}
                                </tr>
                            ))}</tbody>
                        </table>
                    </div>
            )}
        </main>
    );
}

export default Inscripciones;
