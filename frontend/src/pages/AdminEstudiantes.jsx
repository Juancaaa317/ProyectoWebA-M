import { useEffect, useState } from "react";
import { usuarioService } from "../services/panelService";
import "../styles/adminProgramas.css";

function AdminEstudiantes() {
    const [estudiantes, setEstudiantes] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let activa = true;
        const timer = setTimeout(() => {
            usuarioService.listarEstudiantes(busqueda)
                .then((lista) => { if (activa) { setEstudiantes(lista); setError(""); } })
                .catch((err) => { if (activa) setError(err.message); })
                .finally(() => { if (activa) setLoading(false); });
        }, 250);
        return () => { activa = false; clearTimeout(timer); };
    }, [busqueda]);

    return <main className="admin">
        <header className="admin-header"><div><p className="admin-subtitle">PANEL DE ADMINISTRACIÓN</p><h1>Estudiantes</h1></div></header>
        <p className="panel-ayuda">Listado con búsqueda por nombre, correo o tarjeta de identidad. La API todavía no ofrece edición ni eliminación de estudiantes.</p>
        <label className="panel-selector">Buscar estudiante<input type="search" value={busqueda} onChange={(e) => { setLoading(true); setBusqueda(e.target.value); }} placeholder="Nombre, correo o tarjeta" /></label>
        {error && <p className="login-error" role="alert">{error}</p>}
        {loading ? <div className="admin-estado">Cargando estudiantes...</div> : estudiantes.length === 0 ? <p className="admin-vacio">No se encontraron estudiantes.</p> :
            <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>ID</th><th>Nombre</th><th>Tarjeta de identidad</th><th>Correo</th><th>Teléfono</th></tr></thead>
                <tbody>{estudiantes.map((estudiante) => <tr key={estudiante.idEstudiante}><td>{estudiante.idEstudiante}</td><td>{estudiante.nombre}</td><td>{estudiante.tarjetaIdentidad || "—"}</td><td>{estudiante.correo}</td><td>{estudiante.telefono || "—"}</td></tr>)}</tbody>
            </table></div>}
    </main>;
}

export default AdminEstudiantes;
