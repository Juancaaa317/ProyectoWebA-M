import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import ProgramaTable from "../components/ProgramaTable";
import ProgramaForm from "../components/ProgramaForm";
import usePrograms from "../hooks/usePrograms";
import "../styles/login.css";
import "../styles/adminProgramas.css";

function AdminProgramas() {
    const {
        programas,
        loading,
        error,
        saving,
        actionError,
        cargar,
        crear,
        actualizar,
        eliminar,
        limpiarErrorAccion,
    } = usePrograms();

    // null = cerrado | {} = crear | programa = editar
    const [formAbierto, setFormAbierto] = useState(null);
    const [aEliminar, setAEliminar] = useState(null);
    const [mensaje, setMensaje] = useState("");

    // El mensaje de éxito desaparece solo a los 3 segundos
    useEffect(() => {
        if (!mensaje) return;
        const timer = setTimeout(() => setMensaje(""), 3000);
        return () => clearTimeout(timer);
    }, [mensaje]);

    const abrirCrear = () => {
        limpiarErrorAccion();
        setFormAbierto({});
    };

    const abrirEditar = (programa) => {
        limpiarErrorAccion();
        setFormAbierto(programa);
    };

    const cerrarForm = () => {
        limpiarErrorAccion();
        setFormAbierto(null);
    };

    const handleGuardar = async (data) => {
        const editando = Boolean(formAbierto?.idPrograma);
        const ok = editando
            ? await actualizar(formAbierto.idPrograma, data)
            : await crear(data);

        if (ok) {
            setFormAbierto(null);
            setMensaje(editando ? "Programa actualizado." : "Programa creado.");
        }
    };

    const abrirEliminar = (programa) => {
        limpiarErrorAccion();
        setAEliminar(programa);
    };

    const cerrarEliminar = () => {
        limpiarErrorAccion();
        setAEliminar(null);
    };

    const confirmarEliminar = async () => {
        const ok = await eliminar(aEliminar.idPrograma);
        if (ok) {
            setAEliminar(null);
            setMensaje("Programa eliminado.");
        }
    };

    return (
        <>
            <Navbar />

            <main className="admin">
                <div className="admin-header">
                    <div>
                        <p className="admin-subtitle">PANEL DE ADMINISTRACIÓN</p>
                        <h1>Programas</h1>
                    </div>

                    <button
                        type="button"
                        className="login-button admin-btn-nuevo"
                        onClick={abrirCrear}
                        disabled={loading || saving}
                    >
                        + Nuevo programa
                    </button>
                </div>

                {mensaje && <p className="admin-exito">{mensaje}</p>}

                {loading && (
                    <div className="admin-estado">
                        <div className="admin-spinner" aria-label="Cargando" />
                        <p>Cargando programas...</p>
                    </div>
                )}

                {!loading && error && (
                    <div className="admin-estado">
                        <p className="login-error">{error}</p>
                        <button type="button" className="admin-btn-editar" onClick={cargar}>
                            Reintentar
                        </button>
                    </div>
                )}

                {!loading && !error && (
                    <ProgramaTable
                        programas={programas}
                        onEditar={abrirEditar}
                        onEliminar={abrirEliminar}
                        disabled={saving}
                    />
                )}
            </main>

            {/* Modal: crear / editar */}
            {formAbierto && (
                <div className="admin-modal-fondo" onClick={cerrarForm}>
                    <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
                        <ProgramaForm
                            programa={formAbierto.idPrograma ? formAbierto : null}
                            onGuardar={handleGuardar}
                            onCancelar={cerrarForm}
                            saving={saving}
                            error={actionError}
                        />
                    </div>
                </div>
            )}

            {/* Modal: confirmar eliminación */}
            {aEliminar && (
                <div className="admin-modal-fondo" onClick={cerrarEliminar}>
                    <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
                        <h2>Eliminar programa</h2>
                        <p className="admin-confirmar-texto">
                            ¿Seguro que quieres eliminar{" "}
                            <strong>{aEliminar.nombrePrograma}</strong>? Esta acción no se
                            puede deshacer.
                        </p>

                        {actionError && <p className="login-error">{actionError}</p>}

                        <div className="admin-form-botones">
                            <button
                                type="button"
                                className="admin-btn-cancelar"
                                onClick={cerrarEliminar}
                                disabled={saving}
                            >
                                Cancelar
                            </button>
                            <button
                                type="button"
                                className="admin-btn-eliminar"
                                onClick={confirmarEliminar}
                                disabled={saving}
                            >
                                {saving ? "Eliminando..." : "Sí, eliminar"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <Footer />
        </>
    );
}

export default AdminProgramas;