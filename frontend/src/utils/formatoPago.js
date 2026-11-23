export const formatearMonto = (valor) =>
    Number(valor).toLocaleString("es-CO", { minimumFractionDigits: 2, maximumFractionDigits: 2 });

// "2026-11" -> "noviembre de 2026"
export const formatearPeriodo = (periodo) => {
    const [anio, mes] = periodo.split("-").map(Number);
    return new Date(anio, mes - 1, 1).toLocaleDateString("es-CO", { month: "long", year: "numeric" });
};

// Mes actual en el mismo formato que el campo de mes ("2026-11"), con la fecha local
export const periodoActual = () => {
    const hoy = new Date();
    return `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, "0")}`;
};
