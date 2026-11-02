import { apiFetch, ApiError } from "./api";

const USE_MOCK = import.meta.env.VITE_MOCK_PROGRAMAS === "true";

/* ================= MOCK (solo si VITE_MOCK_PROGRAMAS=true) ================= */
let mockDb = [
    { idPrograma: 1, nombrePrograma: "Ballet", descripcion: "Técnica clásica", estadoPrograma: "ACTIVO" },
    { idPrograma: 2, nombrePrograma: "Danza Acrobática", descripcion: "Acrobacia y flexibilidad", estadoPrograma: "ACTIVO" },
    { idPrograma: 3, nombrePrograma: "Danza Árabe", descripcion: "Ritmo y expresión", estadoPrograma: "INACTIVO" },
];
let nextId = 4;
const delay = (ms = 600) => new Promise((r) => setTimeout(r, ms));

const mock = {
    async listar() {
        await delay();
        return [...mockDb];
    },

    async obtener(id) {
        await delay();
        const programa = mockDb.find((p) => p.idPrograma === id);
        if (!programa) throw new ApiError("El programa no existe", 404);
        return programa;
    },

    async crear(data) {
        await delay();
        const nombre = data.nombrePrograma?.trim();
        if (!nombre) throw new ApiError("El nombre es obligatorio", 400);
        if (mockDb.some((p) => p.nombrePrograma.toLowerCase() === nombre.toLowerCase()))
            throw new ApiError("Ya existe un programa con ese nombre", 409);
        const nuevo = { ...data, nombrePrograma: nombre, idPrograma: nextId++ };
        mockDb.push(nuevo);
        return nuevo;
    },

    async actualizar(id, data) {
        await delay();
        const i = mockDb.findIndex((p) => p.idPrograma === id);
        if (i === -1) throw new ApiError("El programa no existe", 404);
        const nombre = data.nombrePrograma?.trim();
        if (!nombre) throw new ApiError("El nombre es obligatorio", 400);
        if (mockDb.some((p) => p.idPrograma !== id && p.nombrePrograma.toLowerCase() === nombre.toLowerCase()))
            throw new ApiError("Ya existe un programa con ese nombre", 409);
        mockDb[i] = { ...mockDb[i], ...data, nombrePrograma: nombre, idPrograma: id };
        return mockDb[i];
    },

    async eliminar(id) {
        await delay();
        if (!mockDb.some((p) => p.idPrograma === id))
            throw new ApiError("El programa no existe", 404);
        mockDb = mockDb.filter((p) => p.idPrograma !== id);
        return null;
    },
};

/* ================= API REAL ================= */
const real = {
    listar: () => apiFetch("/programas"),
    obtener: (id) => apiFetch(`/programas/${id}`),
    crear: (data) =>
        apiFetch("/programas", { method: "POST", body: JSON.stringify(data) }),
    actualizar: (id, data) =>
        apiFetch(`/programas/${id}`, { method: "PUT", body: JSON.stringify(data) }),
    eliminar: (id) => apiFetch(`/programas/${id}`, { method: "DELETE" }),
};

export const programaService = USE_MOCK ? mock : real;