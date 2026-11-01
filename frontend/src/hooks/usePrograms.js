import { useCallback, useEffect, useState } from "react";
import { programaService } from "../services/programaService";
import { useAuth } from "../context/AuthContext";

function usePrograms() {
    const { logout } = useAuth();

    const [programas, setProgramas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [saving, setSaving] = useState(false);
    const [actionError, setActionError] = useState("");

    // Convierte cualquier error en un mensaje; si la sesión expiró, la cierra
    const manejarError = useCallback(
        (err) => {
            if (err.status === 401) logout();
            return err.message || "Ocurrió un error inesperado.";
        },
        [logout]
    );

    const cargar = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const data = await programaService.listar();
            setProgramas(data);
        } catch (err) {
            setError(manejarError(err));
        } finally {
            setLoading(false);
        }
    }, [manejarError]);

    // Carga la lista al abrir la vista
    useEffect(() => {
        cargar();
    }, [cargar]);

    // Ejecuta una operación de escritura con estado "saving" y manejo de errores.
    // Devuelve true si salió bien, false si falló (para que el formulario sepa si cerrarse).
    const ejecutar = async (operacion) => {
        setSaving(true);
        setActionError("");
        try {
            await operacion();
            return true;
        } catch (err) {
            setActionError(manejarError(err));
            return false;
        } finally {
            setSaving(false);
        }
    };

    const crear = (data) =>
        ejecutar(async () => {
            const nuevo = await programaService.crear(data);
            setProgramas((prev) => [...prev, nuevo]);
        });

    const actualizar = (id, data) =>
        ejecutar(async () => {
            const editado = await programaService.actualizar(id, data);
            setProgramas((prev) =>
                prev.map((p) => (p.idPrograma === id ? editado : p))
            );
        });

    const eliminar = (id) =>
        ejecutar(async () => {
            await programaService.eliminar(id);
            setProgramas((prev) => prev.filter((p) => p.idPrograma !== id));
        });

    const limpiarErrorAccion = () => setActionError("");

    return {
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
    };
}

export default usePrograms;