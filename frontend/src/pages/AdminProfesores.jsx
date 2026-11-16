import { useEffect, useState } from "react";
import { usuarioService } from "../services/panelService";
import "../styles/login.css";
import "../styles/adminProgramas.css";

const inicial = { nombre: "", correo: "", telefono: "", contrasena: "", confirmarContrasena: "", areaDocencia: "DOCENTE" };

function AdminProfesores() {
    const [datos, setDatos] = useState(inicial);
    const [creado, setCreado] = useState(null);
    const [error, setError] = useState("");
    const [saving, setSaving] = useState(false);
    const [profesores, setProfesores] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let activa = true;
        const timer = setTimeout(() => {
            usuarioService.listarProfesores(busqueda)
                .then((lista) => { if (activa) { setProfesores(lista); setError(""); } })
                .catch((err) => { if (activa) setError(err.message); })
                .finally(() => { if (activa) setLoading(false); });
        }, 250);
        return () => { activa = false; clearTimeout(timer); };
    }, [busqueda]);

    const actualizar = (campo, valor) => setDatos((actuales) => ({ ...actuales, [campo]: valor }));
    const enviar = async (event) => {
        event.preventDefault();
        setError(""); setCreado(null);
        if (datos.contrasena !== datos.confirmarContrasena) {
            setError("Las contraseñas no coinciden.");
            return;
        }
        setSaving(true);
        try {
            const respuesta = await usuarioService.registrarProfesor({
                ...datos,
                nombre: datos.nombre.trim(),
                correo: datos.correo.trim().toLowerCase(),
                telefono: datos.telefono.trim() || null,
            });
            setCreado(respuesta);
            setDatos(inicial);
            setBusqueda("");
            setProfesores(await usuarioService.listarProfesores());
        } catch (err) { setError(err.message); }
        finally { setSaving(false); }
    };

    return <main className="admin">
        <header className="admin-header"><div><p className="admin-subtitle">PANEL DE ADMINISTRACIÓN</p><h1>Registrar profesor</h1></div></header>
        <p className="panel-ayuda">Al registrarlo por este formulario, la API crea el usuario con rol PROFESOR y su perfil docente. El rol de una cuenta existente no se puede cambiar desde la API actual.</p>
        <section className="panel-listado">
            <h2>Profesores registrados</h2>
            <label className="panel-selector">Buscar por nombre o correo<input type="search" value={busqueda} onChange={(e) => { setLoading(true); setBusqueda(e.target.value); }} placeholder="Nombre o correo" /></label>
            {loading ? <p className="admin-estado">Cargando profesores...</p> : profesores.length === 0 ? <p className="admin-vacio">No se encontraron profesores.</p> :
                <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>ID</th><th>Nombre</th><th>Correo</th><th>Teléfono</th><th>Área</th></tr></thead><tbody>{profesores.map((p) => <tr key={p.idProfesor}><td>{p.idProfesor}</td><td>{p.nombre}</td><td>{p.correo}</td><td>{p.telefono || "—"}</td><td>{p.areaDocencia}</td></tr>)}</tbody></table></div>}
        </section>
        {creado && <div className="admin-exito" role="status">Profesor creado: {creado.nombre} · {creado.correo} · ID de profesor {creado.idProfesor}.</div>}
        {error && <p className="login-error" role="alert">{error}</p>}
        <form className="admin-form panel-form-profesor" onSubmit={enviar}>
            <label>Nombre<input required maxLength="150" value={datos.nombre} onChange={(e) => actualizar("nombre", e.target.value)} /></label>
            <label>Correo<input required type="email" maxLength="150" value={datos.correo} onChange={(e) => actualizar("correo", e.target.value)} /></label>
            <label>Teléfono<input type="tel" pattern="[0-9]{7,15}" title="Usa entre 7 y 15 dígitos" value={datos.telefono} onChange={(e) => actualizar("telefono", e.target.value)} /></label>
            <label>Área docente<select value={datos.areaDocencia} onChange={(e) => actualizar("areaDocencia", e.target.value)}>{["DOCENTE", "PROFESIONAL", "ESPECIALIZADO"].map((area) => <option key={area}>{area}</option>)}</select></label>
            <label>Contraseña<input required type="password" minLength="8" maxLength="72" value={datos.contrasena} onChange={(e) => actualizar("contrasena", e.target.value)} /></label>
            <label>Confirmar contraseña<input required type="password" value={datos.confirmarContrasena} onChange={(e) => actualizar("confirmarContrasena", e.target.value)} /></label>
            <button className="login-button" disabled={saving}>{saving ? "Creando profesor..." : "Crear profesor"}</button>
        </form>
    </main>;
}

export default AdminProfesores;
