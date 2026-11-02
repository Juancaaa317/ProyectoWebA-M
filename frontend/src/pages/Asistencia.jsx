import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ROLES } from "../config/roles";
import { asistenciaService, claseService, sesionService } from "../services/panelService";
import "../styles/login.css";
import "../styles/adminProgramas.css";

const ESTADOS = ["PRESENTE", "AUSENTE", "JUSTIFICADO"];

function Asistencia() {
    const { user } = useAuth();
    const esEstudiante = user.rol === ROLES.ESTUDIANTE;
    const puedeMarcar = user.rol === ROLES.PROFESOR;
    const [clases, setClases] = useState([]);
    const [sesiones, setSesiones] = useState([]);
    const [asistencias, setAsistencias] = useState([]);
    const [asistenciasMias, setAsistenciasMias] = useState([]);
    const [claseId, setClaseId] = useState("");
    const [sesionId, setSesionId] = useState("");
    const [cambios, setCambios] = useState({});
    const [loading, setLoading] = useState(true);
    const [savingId, setSavingId] = useState(null);
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");

    const cargarInicial = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            if (esEstudiante) {
                setAsistenciasMias(await asistenciaService.listarMias());
            } else {
                setClases(await claseService.listar());
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [esEstudiante]);

    useEffect(() => { Promise.resolve().then(cargarInicial); }, [cargarInicial]);

    useEffect(() => {
        if (!claseId) return;
        let activa = true;
        Promise.resolve().then(() => {
            if (activa) setLoading(true);
            return sesionService.listarPorClase(claseId);
        })
            .then((data) => { if (activa) { setSesiones(data); setSesionId(""); setAsistencias([]); } })
            .catch((err) => { if (activa) setError(err.message); })
            .finally(() => { if (activa) setLoading(false); });
        return () => { activa = false; };
    }, [claseId]);

    useEffect(() => {
        if (!sesionId) return;
        let activa = true;
        Promise.resolve().then(() => {
            if (activa) { setLoading(true); setError(""); }
            return asistenciaService.listarSesion(sesionId);
        })
            .then((data) => { if (activa) { setAsistencias(data); setCambios({}); } })
            .catch((err) => { if (activa) setError(err.message); })
            .finally(() => { if (activa) setLoading(false); });
        return () => { activa = false; };
    }, [sesionId]);

    const guardarAsistencia = async (registro) => {
        const cambio = cambios[registro.idInscripcion] ?? {};
        setSavingId(registro.idInscripcion);
        setError("");
        try {
            await asistenciaService.marcar(sesionId, registro.idInscripcion, {
                estado: cambio.estado ?? registro.estado ?? "PRESENTE",
                observaciones: cambio.observaciones ?? registro.observaciones ?? "",
            });
            setMensaje(`Asistencia guardada para ${registro.nombreEstudiante}.`);
            setAsistencias(await asistenciaService.listarSesion(sesionId));
            setCambios({});
        } catch (err) {
            setError(err.message);
        } finally {
            setSavingId(null);
        }
    };

    return (
        <main className="admin">
            <header className="admin-header"><div>
                <p className="admin-subtitle">{esEstudiante ? "PANEL DE ESTUDIANTE" : "CONTROL DE CLASES"}</p>
                <h1>{esEstudiante ? "Mi asistencia" : puedeMarcar ? "Tomar asistencia" : "Asistencia por sesión"}</h1>
            </div></header>
            {mensaje && <p className="admin-exito" role="status">{mensaje}</p>}
            {error && <p className="login-error" role="alert">{error}</p>}

            {esEstudiante ? (
                loading ? <div className="admin-estado">Cargando historial...</div> :
                    asistenciasMias.length === 0 ? <p className="admin-vacio">Aún no hay asistencias registradas.</p> :
                        <div className="admin-tabla-wrapper"><table className="admin-tabla">
                            <thead><tr><th>Clase</th><th>Programa</th><th>Fecha</th><th>Horario</th><th>Estado</th><th>Observaciones</th></tr></thead>
                            <tbody>{asistenciasMias.map((item) => <tr key={item.idAsistencia}>
                                <td>{item.nombreClase}</td><td>{item.nombrePrograma}</td><td>{item.fecha}</td>
                                <td>{item.horaInicio}–{item.horaFin}</td><td>{item.estado}</td><td>{item.observaciones || "—"}</td>
                            </tr>)}</tbody>
                        </table></div>
            ) : (
                <>
                    <div className="panel-selectores">
                        <label>Clase
                        <select value={claseId} onChange={(e) => { setClaseId(e.target.value); setSesionId(""); setAsistencias([]); }}>
                                <option value="">Selecciona una clase</option>
                                {clases.map((clase) => <option key={clase.idClase} value={clase.idClase}>
                                    {clase.nombre} · {clase.nombreProfesor}
                                </option>)}
                            </select>
                        </label>
                        <label>Sesión
                            <select value={sesionId} onChange={(e) => setSesionId(e.target.value)} disabled={!claseId}>
                                <option value="">Selecciona una sesión</option>
                                {sesiones.map((sesion) => <option key={sesion.idSesion} value={sesion.idSesion}>
                                    {sesion.fecha} · {sesion.horaInicio}–{sesion.horaFin} · {sesion.estadoSesion}
                                </option>)}
                            </select>
                        </label>
                    </div>
                    {!loading && sesionId && asistencias.length === 0 && <p className="admin-vacio">No hay estudiantes activos para esta sesión.</p>}
                    {!loading && sesionId && asistencias.length > 0 && <div className="admin-tabla-wrapper"><table className="admin-tabla">
                        <thead><tr><th>Estudiante</th><th>Estado</th><th>Observaciones</th>{puedeMarcar && <th>Acción</th>}</tr></thead>
                        <tbody>{asistencias.map((registro) => {
                            const cambio = cambios[registro.idInscripcion] ?? {};
                            return <tr key={registro.idInscripcion}>
                                <td>{registro.nombreEstudiante}</td>
                                <td>{puedeMarcar ? <select value={cambio.estado ?? registro.estado ?? "PRESENTE"}
                                    onChange={(e) => setCambios((prev) => ({ ...prev, [registro.idInscripcion]: { ...prev[registro.idInscripcion], estado: e.target.value } }))}>
                                    {ESTADOS.map((estado) => <option key={estado}>{estado}</option>)}
                                </select> : (registro.estado ?? "Sin registrar")}</td>
                                <td>{puedeMarcar ? <input maxLength="500" aria-label={`Observación para ${registro.nombreEstudiante}`}
                                    value={cambio.observaciones ?? registro.observaciones ?? ""}
                                    onChange={(e) => setCambios((prev) => ({ ...prev, [registro.idInscripcion]: { ...prev[registro.idInscripcion], observaciones: e.target.value } }))} /> : registro.observaciones || "—"}</td>
                                {puedeMarcar && <td><button className="admin-btn-editar" disabled={savingId === registro.idInscripcion}
                                    onClick={() => guardarAsistencia(registro)}>{savingId === registro.idInscripcion ? "Guardando..." : "Guardar"}</button></td>}
                            </tr>;
                        })}</tbody>
                    </table></div>}
                    {puedeMarcar && <p className="panel-ayuda">La API solo permite al profesor asignado registrar asistencia. Si eliges otra clase, el servidor rechazará la operación.</p>}
                </>
            )}
        </main>
    );
}

export default Asistencia;
