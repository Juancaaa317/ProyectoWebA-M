import { useCallback, useEffect, useState } from "react";

/**
 * Ejecuta una consulta al montar el componente y expone su estado.
 * `cargar` debe ser estable (definida fuera del componente o con useCallback).
 * Devuelve { datos, cargando, error, reintentar }.
 */
function useCarga(cargar) {
    const [estado, setEstado] = useState({ datos: null, cargando: true, error: "" });
    const [intento, setIntento] = useState(0);

    useEffect(() => {
        let activa = true;
        cargar()
            .then((datos) => { if (activa) setEstado({ datos, cargando: false, error: "" }); })
            .catch((err) => {
                if (activa) setEstado({ datos: null, cargando: false, error: err.message || "No se pudo cargar la información." });
            });
        // Si el componente se desmonta antes de que responda la API, se ignora la respuesta
        return () => { activa = false; };
    }, [cargar, intento]);

    const reintentar = useCallback(() => {
        setEstado({ datos: null, cargando: true, error: "" });
        setIntento((n) => n + 1);
    }, []);

    return { ...estado, reintentar };
}

export default useCarga;
