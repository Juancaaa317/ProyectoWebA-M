import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { perfilService } from "../services/perfilService";
import "../styles/login.css";
import "../styles/adminProgramas.css";
import "../styles/perfil.css";

const AREAS = {
    DOCENTE: "Docente",
    PROFESIONAL: "Profesional",
    ESPECIALIZADO: "Especializado",
};

const formatearFecha = (valor) => {
    if (!valor) return "—";
    const [anio, mes, dia] = String(valor).slice(0, 10).split("-").map(Number);
    if (!anio) return "—";
    return new Date(anio, mes - 1, dia).toLocaleDateString("es-CO", {
        day: "numeric", month: "long", year: "numeric",
    });
};

function validarContrasena({ contrasenaActual, contrasenaNueva, confirmarContrasena }) {
    if (!contrasenaActual) return "Escribe tu contraseña actual.";
    if (contrasenaNueva.length < 8) return "La contraseña nueva debe tener al menos 8 caracteres.";
    if (contrasenaNueva !== confirmarContrasena) return "La confirmación no coincide con la contraseña nueva.";
    return "";
}

const CONTRASENA_VACIA = { contrasenaActual: "", contrasenaNueva: "", confirmarContrasena: "" };

function Perfil() {
    const { user, actualizarUsuario } = useAuth();
    const [perfil, setPerfil] = useState(null);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState("");

    const [nombre, setNombre] = useState("");
    const [telefono, setTelefono] = useState("");
    const [guardando, setGuardando] = useState(false);
    const [errorPerfil, setErrorPerfil] = useState("");
    const [mensajePerfil, setMensajePerfil] = useState("");

    const [contrasena, setContrasena] = useState(CONTRASENA_VACIA);
    const [cambiando, setCambiando] = useState(false);
    const [errorContrasena, setErrorContrasena] = useState("");
    const [mensajeContrasena, setMensajeContrasena] = useState("");

    const aplicarPerfil = (datos) => {
        setPerfil(datos);
        setNombre(datos.nombre ?? "");
        setTelefono(datos.telefono ?? "");
    };

    const cargar = useCallback(async () => {
        setLoading(true);
        setLoadError("");
        try {
            const datos = await perfilService.obtener();
            setPerfil(datos);
            setNombre(datos.nombre ?? "");
            setTelefono(datos.telefono ?? "");
        } catch (err) {
            setLoadError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => { Promise.resolve().then(cargar); }, [cargar]);

    const guardarPerfil = async (event) => {
        event.preventDefault();
        setMensajePerfil("");

        const nombreLimpio = nombre.trim();
        const telefonoLimpio = telefono.trim();
        if (!nombreLimpio) return setErrorPerfil("El nombre es obligatorio.");
        if (nombreLimpio.length > 150) return setErrorPerfil("El nombre no puede superar los 150 caracteres.");
        if (telefonoLimpio && !/^[0-9]{7,15}$/.test(telefonoLimpio)) {
            return setErrorPerfil("El teléfono debe tener entre 7 y 15 dígitos, sin espacios ni símbolos.");
        }

        setGuardando(true);
        setErrorPerfil("");
        try {
            // Un teléfono vacío no se envía: el servidor deja el actual
            const datos = { nombre: nombreLimpio, ...(telefonoLimpio && { telefono: telefonoLimpio }) };
            const actualizado = await perfilService.actualizar(datos);
            aplicarPerfil(actualizado);
            actualizarUsuario({ nombre: actualizado.nombre, telefono: actualizado.telefono });
            setMensajePerfil("Cambios guardados.");
        } catch (err) {
            setErrorPerfil(err.message);
        } finally {
            setGuardando(false);
        }
    };

    const cambiarCampoContrasena = (campo) => (event) =>
        setContrasena((actual) => ({ ...actual, [campo]: event.target.value }));

    const guardarContrasena = async (event) => {
        event.preventDefault();
        setMensajeContrasena("");

        const problema = validarContrasena(contrasena);
        if (problema) return setErrorContrasena(problema);

        setCambiando(true);
        setErrorContrasena("");
        try {
            await perfilService.cambiarContrasena(contrasena);
            setContrasena(CONTRASENA_VACIA);
            setMensajeContrasena("Contraseña actualizada.");
        } catch (err) {
            setErrorContrasena(err.message);
        } finally {
            setCambiando(false);
        }
    };

    const inicial = (perfil?.nombre || user.nombre || user.correo || "?").charAt(0).toUpperCase();

    return (
        <main className="perfil">
            <div className="perfil-columna">
                <header className="perfil-cabecera">
                    <div className="perfil-avatar" aria-hidden="true">{inicial}</div>
                    <div>
                        <h1>Mi perfil</h1>
                        <p className="perfil-correo">{perfil?.correo ?? user.correo}</p>
                    </div>
                </header>

                {loadError && (
                    <p className="login-error" role="alert">
                        {loadError} <button type="button" className="admin-btn-cancelar" onClick={cargar}>Reintentar</button>
                    </p>
                )}

                {loading && <div className="admin-estado">Cargando tu perfil...</div>}

                {perfil && (
                    <>
                        <section className="perfil-card" aria-labelledby="perfil-datos">
                            <h2 id="perfil-datos">Mis datos</h2>

                            <form onSubmit={guardarPerfil} noValidate className="perfil-form">
                                {mensajePerfil && <p className="admin-exito" role="status">{mensajePerfil}</p>}
                                {errorPerfil && <p className="login-error" role="alert">{errorPerfil}</p>}

                                <label htmlFor="perfil-nombre">Nombre
                                    <input id="perfil-nombre" value={nombre} maxLength={150}
                                        onChange={(e) => setNombre(e.target.value)} required />
                                </label>
                                <label htmlFor="perfil-telefono">Teléfono
                                    <input id="perfil-telefono" type="tel" inputMode="numeric" value={telefono}
                                        onChange={(e) => setTelefono(e.target.value)} placeholder="3001234567" />
                                    <span className="perfil-ayuda">Solo dígitos, entre 7 y 15.</span>
                                </label>

                                <button className="login-button" disabled={guardando}>
                                    {guardando ? "Guardando..." : "Guardar cambios"}
                                </button>
                            </form>

                            <dl className="perfil-datos-fijos">
                                <dt>Correo</dt>
                                <dd>{perfil.correo}</dd>
                                <dt>Rol</dt>
                                <dd>{perfil.rol}</dd>
                                {perfil.tarjetaIdentidad && (<><dt>Tarjeta de identidad</dt><dd>{perfil.tarjetaIdentidad}</dd></>)}
                                {perfil.fechaNacimiento && (<><dt>Fecha de nacimiento</dt><dd>{formatearFecha(perfil.fechaNacimiento)}</dd></>)}
                                {perfil.areaDocencia && (<><dt>Área de docencia</dt><dd>{AREAS[perfil.areaDocencia] ?? perfil.areaDocencia}</dd></>)}
                            </dl>
                        </section>

                        <section className="perfil-card" aria-labelledby="perfil-clave">
                            <h2 id="perfil-clave">Cambiar contraseña</h2>

                            <form onSubmit={guardarContrasena} noValidate className="perfil-form">
                                {mensajeContrasena && <p className="admin-exito" role="status">{mensajeContrasena}</p>}
                                {errorContrasena && <p className="login-error" role="alert">{errorContrasena}</p>}

                                <label htmlFor="perfil-actual">Contraseña actual
                                    <input id="perfil-actual" type="password" autoComplete="current-password"
                                        value={contrasena.contrasenaActual} onChange={cambiarCampoContrasena("contrasenaActual")} />
                                </label>
                                <label htmlFor="perfil-nueva">Contraseña nueva
                                    <input id="perfil-nueva" type="password" autoComplete="new-password" maxLength={72}
                                        value={contrasena.contrasenaNueva} onChange={cambiarCampoContrasena("contrasenaNueva")} />
                                    <span className="perfil-ayuda">Mínimo 8 caracteres.</span>
                                </label>
                                <label htmlFor="perfil-confirmar">Repite la contraseña nueva
                                    <input id="perfil-confirmar" type="password" autoComplete="new-password" maxLength={72}
                                        value={contrasena.confirmarContrasena} onChange={cambiarCampoContrasena("confirmarContrasena")} />
                                </label>

                                <button className="login-button" disabled={cambiando}>
                                    {cambiando ? "Cambiando..." : "Cambiar contraseña"}
                                </button>
                            </form>
                        </section>
                    </>
                )}
            </div>
        </main>
    );
}

export default Perfil;
