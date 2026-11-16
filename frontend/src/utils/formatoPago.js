export const formatearMonto = (valor) =>
    Number(valor).toLocaleString("es-CO", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
