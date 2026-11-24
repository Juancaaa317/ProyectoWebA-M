import { apiFetch } from "./api";

// Consultas públicas de la oferta académica (no requieren sesión).
// Las usan las páginas de la landing: Clases, Horario y Profesores.
export const ofertaService = {
    programasActivos: () => apiFetch("/programas/estado/ACTIVO"),
    clases: () => apiFetch("/clases"),
    horarios: () => apiFetch("/horarios"),
    // Solo nombre, área y disciplinas: el backend no expone datos de contacto
    profesores: () => apiFetch("/profesores"),
};

// Una clase se muestra al público si está abierta o por empezar
export const CLASE_ABIERTA = new Set(["EN_CURSO", "PROGRAMADA"]);
