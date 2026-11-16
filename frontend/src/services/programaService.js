import { apiFetch } from "./api";

export const programaService = {
    listar: () => apiFetch("/programas"),
    crear: (data) =>
        apiFetch("/programas", { method: "POST", body: JSON.stringify(data) }),
    actualizar: (id, data) =>
        apiFetch(`/programas/${id}`, { method: "PUT", body: JSON.stringify(data) }),
    eliminar: (id) => apiFetch(`/programas/${id}`, { method: "DELETE" }),
};
