import { apiFetch } from "./api";

export const claseService = {
    listar: () => apiFetch("/clases"),
    crear: (datos) => apiFetch("/clases", { method: "POST", body: JSON.stringify(datos) }),
    actualizar: (id, datos) => apiFetch(`/clases/${id}`, { method: "PUT", body: JSON.stringify(datos) }),
    eliminar: (id) => apiFetch(`/clases/${id}`, { method: "DELETE" }),
};

export const horarioService = {
    listarPorClase: (idClase) => apiFetch(`/horarios/clase/${idClase}`),
    crear: (datos) => apiFetch("/horarios", { method: "POST", body: JSON.stringify(datos) }),
    actualizar: (id, datos) => apiFetch(`/horarios/${id}`, { method: "PUT", body: JSON.stringify(datos) }),
    eliminar: (id) => apiFetch(`/horarios/${id}`, { method: "DELETE" }),
};

export const inscripcionService = {
    listarMias: () => apiFetch("/inscripciones/mias"),
    listarTodas: () => apiFetch("/inscripciones"),
    crear: (idClase) => apiFetch("/inscripciones", { method: "POST", body: JSON.stringify({ idClase }) }),
    cambiarEstado: (id, estadoInscripcion) => apiFetch(`/inscripciones/${id}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estadoInscripcion }),
    }),
};

export const asistenciaService = {
    listarMias: () => apiFetch("/asistencias/mias"),
    listarSesion: (idSesion) => apiFetch(`/sesiones/${idSesion}/asistencias`),
    marcar: (idSesion, idInscripcion, datos) => apiFetch(
        `/sesiones/${idSesion}/asistencias/${idInscripcion}`,
        { method: "PUT", body: JSON.stringify(datos) }
    ),
};

export const sesionService = {
    listarPorClase: (idClase) => apiFetch(`/sesiones/clase/${idClase}`),
    crear: (idHorario, fecha) => apiFetch("/sesiones", { method: "POST", body: JSON.stringify({ idHorario: Number(idHorario), fecha }) }),
    cambiarEstado: (id, estadoSesion) => apiFetch(`/sesiones/${id}/estado`, { method: "PATCH", body: JSON.stringify({ estadoSesion }) }),
};

export const catalogoService = {
    programas: () => apiFetch("/programas"),
};

export const usuarioService = {
    registrarProfesor: (datos) => apiFetch("/usuarios/profesores", {
        method: "POST",
        body: JSON.stringify(datos),
    }),
    listarProfesores: (buscar = "") => apiFetch(`/usuarios/profesores${buscar ? `?buscar=${encodeURIComponent(buscar)}` : ""}`),
    listarEstudiantes: (buscar = "") => apiFetch(`/usuarios/estudiantes${buscar ? `?buscar=${encodeURIComponent(buscar)}` : ""}`),
};
