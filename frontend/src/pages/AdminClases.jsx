import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ROLES } from "../config/roles";
import { claseService, horarioService, catalogoService, sesionService, usuarioService } from "../services/panelService";
import "../styles/login.css";
import "../styles/adminProgramas.css";

const claseVacia = { idProfesor: "", idPrograma: "", fechaInicio: "", fechaFinalizacion: "", nombre: "", cupoMaximo: 10, estadoClase: "PROGRAMADA" };
const horarioVacio = { diaSemana: "LUNES", horaInicio: "", horaFin: "" };
const DIAS = ["LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"];
const ESTADOS_SESION = ["PROGRAMADA", "REALIZADA", "CANCELADA"];

function estadosSesionPermitidos(estadoActual) {
    return estadoActual === "PROGRAMADA" ? ESTADOS_SESION : [estadoActual];
}

function diaDeLaFecha(fecha) {
    if (!fecha) return "";
    const [anio, mes, dia] = fecha.split("-").map(Number);
    const fechaUTC = new Date(Date.UTC(anio, mes - 1, dia));
    return DIAS[(fechaUTC.getUTCDay() + 6) % 7];
}

function normalizarFecha(fecha) {
    return fecha ? String(fecha).slice(0, 10) : "";
}

function AdminClases() {
    const { user } = useAuth();
    const esAdmin = user.rol === ROLES.ADMIN;
    const [clases, setClases] = useState([]);
    const [programas, setProgramas] = useState([]);
    const [profesores, setProfesores] = useState([]);
    const [horarios, setHorarios] = useState([]);
    const [sesiones, setSesiones] = useState([]);
    const [claseForm, setClaseForm] = useState(null);
    const [horarioForm, setHorarioForm] = useState(null);
    const [sesionFecha, setSesionFecha] = useState("");
    const [horarioSesionId, setHorarioSesionId] = useState("");
    const [claseSeleccionada, setClaseSeleccionada] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");

    const claseSesion = clases.find((clase) => String(clase.idClase) === claseSeleccionada);
    const fechaMinimaSesion = normalizarFecha(claseSesion?.fechaInicio);
    const fechaMaximaSesion = normalizarFecha(claseSesion?.fechaFinalizacion);
    const fueraDelRangoDeClase = Boolean(sesionFecha && (
        (fechaMinimaSesion && sesionFecha < fechaMinimaSesion) ||
        (fechaMaximaSesion && sesionFecha > fechaMaximaSesion)
    ));
    const horarioDeSesion = horarios.find((horario) => String(horario.idHorario) === horarioSesionId);
    const diaNoCoincide = Boolean(sesionFecha && horarioDeSesion && diaDeLaFecha(sesionFecha) !== horarioDeSesion.diaSemana);

    const cargar = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const [listaClases, listaProgramas, listaProfesores] = await Promise.all([
                claseService.listar(), catalogoService.programas(), esAdmin ? usuarioService.listarProfesores() : Promise.resolve([]),
            ]);
            setClases(listaClases);
            setProgramas(listaProgramas);
            setProfesores(listaProfesores);
            setClaseSeleccionada((actual) => actual && listaClases.some((clase) => String(clase.idClase) === actual)
                ? actual
                : listaClases.length ? String(listaClases[0].idClase) : "");
        } catch (err) { setError(err.message); }
        finally { setLoading(false); }
    }, [esAdmin]);

    useEffect(() => { Promise.resolve().then(cargar); }, [cargar]);

    useEffect(() => {
        if (!claseSeleccionada) return;
        let activa = true;
        horarioService.listarPorClase(claseSeleccionada)
            .then((data) => {
                if (activa) {
                    setHorarios(data);
                    setHorarioSesionId(data.length ? String(data[0].idHorario) : "");
                }
            })
            .catch((err) => { if (activa) setError(err.message); });
        return () => { activa = false; };
    }, [claseSeleccionada]);

    useEffect(() => {
        if (!claseSeleccionada) return;
        let activa = true;
        sesionService.listarPorClase(claseSeleccionada)
            .then((data) => { if (activa) setSesiones(data); })
            .catch((err) => { if (activa) setError(err.message); });
        return () => { activa = false; };
    }, [claseSeleccionada]);

    const guardarClase = async (event) => {
        event.preventDefault();
        setSaving(true); setError("");
        const payload = { ...claseForm, idProfesor: Number(claseForm.idProfesor), idPrograma: Number(claseForm.idPrograma), cupoMaximo: Number(claseForm.cupoMaximo), fechaInicio: claseForm.fechaInicio || null, fechaFinalizacion: claseForm.fechaFinalizacion || null };
        try {
            if (claseForm.idClase) await claseService.actualizar(claseForm.idClase, payload);
            else await claseService.crear(payload);
            setMensaje(claseForm.idClase ? "Clase actualizada." : "Clase creada.");
            setClaseForm(null);
            await cargar();
        } catch (err) { setError(err.message); }
        finally { setSaving(false); }
    };

    const eliminarClase = async (clase) => {
        if (!window.confirm(`¿Eliminar la clase “${clase.nombre}”?`)) return;
        setError("");
        try {
            await claseService.eliminar(clase.idClase);
            if (String(clase.idClase) === claseSeleccionada) setClaseSeleccionada("");
            setMensaje("Clase eliminada.");
            await cargar();
        } catch (err) { setError(err.message); }
    };

    const guardarHorario = async (event) => {
        event.preventDefault();
        setSaving(true); setError("");
        const payload = { ...horarioForm, idClase: Number(claseSeleccionada), horaInicio: `${horarioForm.horaInicio}:00`, horaFin: `${horarioForm.horaFin}:00` };
        try {
            if (horarioForm.idHorario) await horarioService.actualizar(horarioForm.idHorario, payload);
            else await horarioService.crear(payload);
            setMensaje(horarioForm.idHorario ? "Horario actualizado." : "Horario creado.");
            setHorarioForm(null);
            setHorarios(await horarioService.listarPorClase(claseSeleccionada));
        } catch (err) { setError(err.message); }
        finally { setSaving(false); }
    };

    const eliminarHorario = async (horario) => {
        if (!window.confirm("¿Eliminar este horario?")) return;
        setError("");
        try {
            await horarioService.eliminar(horario.idHorario);
            setHorarios(await horarioService.listarPorClase(claseSeleccionada));
            setMensaje("Horario eliminado.");
        } catch (err) { setError(err.message); }
    };

    const crearSesion = async (event) => {
        event.preventDefault();
        const horarioSesion = horarios.find((horario) => String(horario.idHorario) === horarioSesionId);
        if (fueraDelRangoDeClase) {
            setError("La fecha debe estar dentro del periodo configurado para la clase.");
            return;
        }
        if (horarioSesion && diaDeLaFecha(sesionFecha) !== horarioSesion.diaSemana) {
            setError(`Selecciona una fecha que sea ${horarioSesion.diaSemana.toLowerCase()}.`);
            return;
        }
        setSaving(true); setError("");
        try {
            await sesionService.crear(horarioSesionId, sesionFecha);
            setMensaje("Sesión programada.");
            setSesionFecha("");
            setSesiones(await sesionService.listarPorClase(claseSeleccionada));
        } catch (err) { setError(err.message); }
        finally { setSaving(false); }
    };

    const cambiarEstadoSesion = async (id, estado) => {
        setError("");
        try {
            await sesionService.cambiarEstado(id, estado);
            setSesiones(await sesionService.listarPorClase(claseSeleccionada));
            setMensaje("Estado de sesión actualizado.");
        } catch (err) { setError(err.message); }
    };

    const abrirEdicion = (clase) => setClaseForm({
        ...clase,
        fechaInicio: clase.fechaInicio ?? "",
        fechaFinalizacion: clase.fechaFinalizacion ?? "",
    });

    return <main className="admin">
        <header className="admin-header"><div><p className="admin-subtitle">{esAdmin ? "PANEL DE ADMINISTRACIÓN" : "CLASES DE LA ACADEMIA"}</p><h1>{esAdmin ? "Clases y horarios" : "Mis clases"}</h1></div>
            {esAdmin && <button className="login-button" onClick={() => setClaseForm({ ...claseVacia })}>Nueva clase</button>}
        </header>
        {!esAdmin && <p className="panel-ayuda">El servicio actual no ofrece un listado de clases asignadas al profesor. Aquí puedes consultar clases y horarios; la API valida que seas el profesor asignado antes de permitir registrar asistencia.</p>}
        {mensaje && <p className="admin-exito" role="status">{mensaje}</p>}
        {error && <p className="login-error" role="alert">{error}</p>}
        {loading ? <div className="admin-estado">Cargando clases...</div> : clases.length === 0 ? <p className="admin-vacio">No hay clases registradas.</p> :
            <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>Clase</th><th>Programa</th><th>Profesor</th><th>Cupo</th><th>Estado</th><th>Acciones</th></tr></thead>
                <tbody>{clases.map((clase) => <tr key={clase.idClase}>
                    <td>{clase.nombre}</td><td>{clase.nombrePrograma}</td><td>{clase.nombreProfesor}</td><td>{clase.cupoMaximo}</td><td>{clase.estadoClase}</td>
                    <td className="admin-acciones">{esAdmin && <><button className="admin-btn-editar" onClick={() => abrirEdicion(clase)}>Editar</button>
                        <button className="admin-btn-eliminar" onClick={() => eliminarClase(clase)}>Eliminar</button></>}
                        <button className="admin-btn-cancelar" onClick={() => setClaseSeleccionada(String(clase.idClase))}>Horarios</button></td>
                </tr>)}</tbody></table></div>}

        {claseForm && <div className="admin-modal-fondo" onClick={() => setClaseForm(null)}><form className="admin-modal admin-form" onSubmit={guardarClase} onClick={(e) => e.stopPropagation()}>
            <h2>{claseForm.idClase ? "Editar clase" : "Crear clase"}</h2>
            <label>Nombre<input required maxLength="150" value={claseForm.nombre} onChange={(e) => setClaseForm({ ...claseForm, nombre: e.target.value })} /></label>
            <label>Profesor<select required value={claseForm.idProfesor} onChange={(e) => setClaseForm({ ...claseForm, idProfesor: e.target.value })}><option value="">Selecciona...</option>{profesores.map((profesor) => <option key={profesor.idProfesor} value={profesor.idProfesor}>{profesor.nombre} · ID {profesor.idProfesor}</option>)}</select></label>
            <label>Programa<select required value={claseForm.idPrograma} onChange={(e) => setClaseForm({ ...claseForm, idPrograma: e.target.value })}><option value="">Selecciona...</option>{programas.map((p) => <option key={p.idPrograma} value={p.idPrograma}>{p.nombrePrograma}</option>)}</select></label>
            <label>Fecha de inicio<input type="date" value={claseForm.fechaInicio} onChange={(e) => setClaseForm({ ...claseForm, fechaInicio: e.target.value || null })} /></label>
            <label>Fecha de finalización<input type="date" value={claseForm.fechaFinalizacion} onChange={(e) => setClaseForm({ ...claseForm, fechaFinalizacion: e.target.value || null })} /></label>
            <label>Cupo máximo<input required type="number" min="1" value={claseForm.cupoMaximo} onChange={(e) => setClaseForm({ ...claseForm, cupoMaximo: e.target.value })} /></label>
            <label>Estado<select value={claseForm.estadoClase ?? "PROGRAMADA"} onChange={(e) => setClaseForm({ ...claseForm, estadoClase: e.target.value })}>{["PROGRAMADA", "EN_CURSO", "FINALIZADA", "CANCELADA"].map((e) => <option key={e}>{e}</option>)}</select></label>
            {error && <p className="login-error">{error}</p>}
            <div className="admin-form-botones"><button type="button" className="admin-btn-cancelar" onClick={() => setClaseForm(null)}>Cancelar</button><button className="login-button" disabled={saving}>{saving ? "Guardando..." : "Guardar"}</button></div>
        </form></div>}

        {esAdmin && <section className="panel-horarios">
            <header className="admin-header"><div><p className="admin-subtitle">PROGRAMACIÓN SEMANAL</p><h2>Horarios de una clase</h2></div>
                <label>Clase<select value={claseSeleccionada} onChange={(e) => setClaseSeleccionada(e.target.value)}><option value="">Selecciona una clase</option>{clases.map((c) => <option key={c.idClase} value={c.idClase}>{c.nombre}</option>)}</select></label>
                <button className="admin-btn-editar" disabled={!claseSeleccionada} onClick={() => setHorarioForm({ ...horarioVacio })}>Agregar horario</button>
            </header>
            {horarios.length > 0 && <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>Día</th><th>Inicio</th><th>Fin</th><th>Acciones</th></tr></thead><tbody>
                {horarios.map((h) => <tr key={h.idHorario}><td>{h.diaSemana}</td><td>{String(h.horaInicio).slice(0, 5)}</td><td>{String(h.horaFin).slice(0, 5)}</td><td className="admin-acciones">
                    <button className="admin-btn-editar" onClick={() => setHorarioForm({ idHorario: h.idHorario, diaSemana: h.diaSemana, horaInicio: String(h.horaInicio).slice(0, 5), horaFin: String(h.horaFin).slice(0, 5) })}>Editar</button>
                    <button className="admin-btn-eliminar" onClick={() => eliminarHorario(h)}>Eliminar</button></td></tr>)}
            </tbody></table></div>}
            {claseSeleccionada && horarios.length === 0 && <p className="admin-vacio">Esta clase todavía no tiene horarios.</p>}
        </section>}
        {esAdmin && <section className="panel-horarios">
            <header className="admin-header"><div><p className="admin-subtitle">ASISTENCIA</p><h2>Sesiones de clase</h2></div>
                <label>Clase<select value={claseSeleccionada} onChange={(e) => setClaseSeleccionada(e.target.value)}><option value="">Selecciona una clase</option>{clases.map((c) => <option key={c.idClase} value={c.idClase}>{c.nombre}</option>)}</select></label>
            </header>
            {claseSeleccionada && horarios.length > 0 && <form className="panel-form-inline" onSubmit={crearSesion}>
                <label>Horario<select name="horarioId" required value={horarioSesionId} onChange={(e) => { setHorarioSesionId(e.target.value); setError(""); }}>{horarios.map((h) => <option key={h.idHorario} value={h.idHorario}>{h.diaSemana} · {String(h.horaInicio).slice(0, 5)}–{String(h.horaFin).slice(0, 5)}</option>)}</select></label>
                <label>Fecha<input type="date" required min={fechaMinimaSesion || undefined} max={fechaMaximaSesion || undefined} value={sesionFecha} onChange={(e) => { setSesionFecha(e.target.value); setError(""); }} /></label>
                <button className="login-button" disabled={saving || !sesionFecha || fueraDelRangoDeClase || diaNoCoincide}>{saving ? "Guardando..." : "Programar sesión"}</button>
                {fueraDelRangoDeClase && <p className="panel-ayuda">La fecha debe estar dentro del periodo de la clase: {fechaMinimaSesion || "sin fecha inicial"} a {fechaMaximaSesion || "sin fecha final"}.</p>}
                {!fueraDelRangoDeClase && diaNoCoincide && <p className="panel-ayuda">La fecha debe ser {horarioDeSesion.diaSemana.toLowerCase()} para el horario seleccionado.</p>}
            </form>}
            {sesiones.length > 0 && <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>Fecha</th><th>Clase</th><th>Horario</th><th>Estado</th></tr></thead><tbody>{sesiones.map((s) => <tr key={s.idSesion}>
                <td>{s.fecha}</td><td>{s.nombreClase}</td><td>{s.horaInicio}–{s.horaFin}</td><td><select value={s.estadoSesion} onChange={(e) => cambiarEstadoSesion(s.idSesion, e.target.value)}>{estadosSesionPermitidos(s.estadoSesion).map((estado) => <option key={estado}>{estado}</option>)}</select></td>
            </tr>)}</tbody></table></div>}
            {claseSeleccionada && sesiones.length === 0 && <p className="admin-vacio">No hay sesiones programadas.</p>}
        </section>}
        {!esAdmin && <section className="panel-horarios">
            <header className="admin-header"><div><p className="admin-subtitle">CONSULTA</p><h2>Horarios</h2></div>
                <label>Clase<select value={claseSeleccionada} onChange={(e) => setClaseSeleccionada(e.target.value)}><option value="">Selecciona una clase</option>{clases.map((c) => <option key={c.idClase} value={c.idClase}>{c.nombre}</option>)}</select></label>
            </header>
            {horarios.length ? <div className="admin-tabla-wrapper"><table className="admin-tabla"><thead><tr><th>Día</th><th>Inicio</th><th>Fin</th></tr></thead><tbody>{horarios.map((h) => <tr key={h.idHorario}><td>{h.diaSemana}</td><td>{String(h.horaInicio).slice(0, 5)}</td><td>{String(h.horaFin).slice(0, 5)}</td></tr>)}</tbody></table></div> : claseSeleccionada && <p className="admin-vacio">Esta clase todavía no tiene horarios.</p>}
        </section>}

        {horarioForm && <div className="admin-modal-fondo" onClick={() => setHorarioForm(null)}><form className="admin-modal admin-form" onSubmit={guardarHorario} onClick={(e) => e.stopPropagation()}>
            <h2>{horarioForm.idHorario ? "Editar horario" : "Agregar horario"}</h2>
            <label>Día<select value={horarioForm.diaSemana} onChange={(e) => setHorarioForm({ ...horarioForm, diaSemana: e.target.value })}>{DIAS.map((dia) => <option key={dia}>{dia}</option>)}</select></label>
            <label>Hora de inicio<input type="time" required value={horarioForm.horaInicio} onChange={(e) => setHorarioForm({ ...horarioForm, horaInicio: e.target.value })} /></label>
            <label>Hora de finalización<input type="time" required value={horarioForm.horaFin} onChange={(e) => setHorarioForm({ ...horarioForm, horaFin: e.target.value })} /></label>
            {error && <p className="login-error">{error}</p>}
            <div className="admin-form-botones"><button type="button" className="admin-btn-cancelar" onClick={() => setHorarioForm(null)}>Cancelar</button><button className="login-button" disabled={saving}>{saving ? "Guardando..." : "Guardar"}</button></div>
        </form></div>}
    </main>;
}

export default AdminClases;
