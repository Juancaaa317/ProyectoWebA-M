import { useState } from "react";

const MAX_NOMBRE = 100;
const MAX_DESCRIPCION = 255;

function ProgramaForm({ programa, onGuardar, onCancelar, saving, error }) {
    const editando = Boolean(programa);

    const [nombre, setNombre] = useState(programa?.nombrePrograma ?? "");
    const [descripcion, setDescripcion] = useState(programa?.descripcion ?? "");
    const [activo, setActivo] = useState(
        programa ? programa.estadoPrograma === "ACTIVO" : true
    );
    const [errorLocal, setErrorLocal] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        const nombreLimpio = nombre.trim();

        if (!nombreLimpio) {
            setErrorLocal("El nombre es obligatorio.");
            return;
        }
        if (nombreLimpio.length > MAX_NOMBRE) {
            setErrorLocal(`El nombre no puede superar ${MAX_NOMBRE} caracteres.`);
            return;
        }
        if (descripcion.length > MAX_DESCRIPCION) {
            setErrorLocal(
                `La descripción no puede superar ${MAX_DESCRIPCION} caracteres.`
            );
            return;
        }

        setErrorLocal("");
        await onGuardar({
            nombrePrograma: nombreLimpio,
            descripcion: descripcion.trim(),
            estadoPrograma: activo ? "ACTIVO" : "INACTIVO",
        });
    };

    const mensajeError = errorLocal || error;

    return (
        <form className="admin-form" onSubmit={handleSubmit}>
            <h2>{editando ? "Editar programa" : "Nuevo programa"}</h2>

            <div className="form-group">
                <label htmlFor="nombrePrograma">Nombre</label>
                <input
                    type="text"
                    id="nombrePrograma"
                    placeholder="Ej: Ballet"
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                    disabled={saving}
                    autoFocus
                />
            </div>

            <div className="form-group">
                <label htmlFor="descripcionPrograma">Descripción</label>
                <textarea
                    id="descripcionPrograma"
                    placeholder="Describe brevemente el programa"
                    rows={3}
                    value={descripcion}
                    onChange={(e) => setDescripcion(e.target.value)}
                    disabled={saving}
                />
            </div>

            <label className="admin-check">
                <input
                    type="checkbox"
                    checked={activo}
                    onChange={(e) => setActivo(e.target.checked)}
                    disabled={saving}
                />
                Programa activo
            </label>

            {mensajeError && <p className="login-error">{mensajeError}</p>}

            <div className="admin-form-botones">
                <button
                    type="button"
                    className="admin-btn-cancelar"
                    onClick={onCancelar}
                    disabled={saving}
                >
                    Cancelar
                </button>
                <button type="submit" className="login-button" disabled={saving}>
                    {saving
                        ? "Guardando..."
                        : editando
                            ? "Guardar cambios"
                            : "Crear programa"}
                </button>
            </div>
        </form>
    );
}

export default ProgramaForm;