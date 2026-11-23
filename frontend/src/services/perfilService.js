import { apiFetch } from "./api";

// Perfil del usuario autenticado: /api/usuarios/me
export const perfilService = {
    obtener: () => apiFetch("/usuarios/me"),
    actualizar: (datos) => apiFetch("/usuarios/me", { method: "PUT", body: JSON.stringify(datos) }),
    cambiarContrasena: (datos) =>
        apiFetch("/usuarios/me/contrasena", { method: "PUT", body: JSON.stringify(datos) }),
};
