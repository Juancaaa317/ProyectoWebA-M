// El backend devuelve algunas fechas como "2026-11-22T05:00:00.000Z" y otras como "2026-11-22".
// Para mostrarlas basta la parte de la fecha: "2026-11-22"
export const soloFecha = (valor) => (valor ? String(valor).slice(0, 10) : "—");
