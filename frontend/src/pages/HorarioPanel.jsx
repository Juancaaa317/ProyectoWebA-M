import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ROLES } from "../config/roles";
import { claseService, horarioService, inscripcionService } from "../services/panelService";
import "../styles/login.css";
import "../styles/adminProgramas.css";

function HorarioPanel() {
    const { user } = useAuth();
    const esEstudiante = user.rol === ROLES.ESTUDIANTE;
    const [clases, setClases] = useState([]);
    const [horarios, setHorarios] = useState([]);
    const [claseId, setClaseId] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let activa = true;
        const cargar = async () => {
            setLoading(true);
            try {
                if (esEstudiante) {
                    const [inscripciones, todasLasClases] = await Promise.all([
                        inscripcionService.listarMias(), claseService.listar(),
                    ]);
                    const idClases = new Set(inscripciones.filter((i) => i.estadoInscripcion === "ACTIVA").map((i) => i.idClase));
                    const propias = todasLasClases.filter((c) => idClases.has(c.idClase));
                    if (activa) { setClases(propias); setClaseId(propias.length === 1 ? String(propias[0].idClase) : ""); }
                } else {
                    const todas = await claseService.listar();
                    if (activa) setClases(todas);
                }
            } catch (err) { if (activa) setError(err.message); }
            finally { if (activa) setLoading(false); }
        };
        cargar();
        return () => { activa = false; };
    }, [esEstudiante]);

    useEffect(() => {
        if (!claseId) return;
        let activa = true;
        Promise.resolve().then(() => horarioService.listarPorClase(claseId))
            .then((data) => { if (activa) setHorarios(data); })
            .catch((err) => { if (activa) setError(err.message); });
        return () => { activa = false; };
    }, [claseId]);

    return <main className="admin">
        <header className="admin-header"><div><p className="admin-subtitle">PANEL PERSONAL</p><h1>{esEstudiante ? "Mi horario" : "Horario de clases"}</h1></div></header>
        {error && <p className="login-error" role="alert">{error}</p>}
        {!esEstudiante && <p className="panel-ayuda">La API aún no tiene una consulta de horarios filtrada por profesor. Puedes consultar horarios por clase; el registro de asistencia sí comprueba la asignación.</p>}
        {loading ? <div className="admin-estado">Cargando horarios...</div> : esEstudiante && clases.length === 0 ? <p className="admin-vacio">No tienes clases con inscripción activa.</p> : <>
            <label className="panel-selector">Clase<select value={claseId} onChange={(e) => setClaseId(e.target.value)}><option value="">Selecciona una clase</option>{clases.map((c) => <option key={c.idClase} value={c.idClase}>{c.nombre} · {c.nombrePrograma}</option>)}</select></label>
            {claseId && (horarios.length ? <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>Día</th><th>Clase</th><th>Programa</th><th>Hora de inicio</th><th>Hora de fin</th></tr></thead><tbody>{horarios.map((h) => <tr key={h.idHorario}><td>{h.diaSemana}</td><td>{h.nombreClase}</td><td>{h.nombrePrograma}</td><td>{String(h.horaInicio).slice(0, 5)}</td><td>{String(h.horaFin).slice(0, 5)}</td></tr>)}</tbody></table></div> : <p className="admin-vacio">No hay horarios registrados para esta clase.</p>)}
        </>}
    </main>;
}

export default HorarioPanel;
