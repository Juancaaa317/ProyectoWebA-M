import { apiFetch, ApiError } from "./api";

const USE_MOCK = import.meta.env.VITE_USE_MOCK === "true";
const delay = (ms = 500) => new Promise((resolve) => setTimeout(resolve, ms));

const mockUsers = [
    {
        id: 1,
        correo: "admin@arteymovimiento.com",
        password: "admin123",
        rol: "ADMIN",
    },
];
let nextMockId = 2;

const mock = {
    async iniciarSesion(correo, password) {
        await delay();
        const usuario = mockUsers.find(
            (item) =>
                item.correo.toLowerCase() === correo.toLowerCase() &&
                item.password === password
        );

        if (!usuario) {
            throw new ApiError("Correo o contraseña incorrectos", 401);
        }

        const datosUsuario = { ...usuario };
        delete datosUsuario.password;
        return { token: "mock-token", usuario: datosUsuario };
    },

    async registrarse(datos) {
        await delay();
        const correo = datos.correo.trim().toLowerCase();

        if (mockUsers.some((item) => item.correo.toLowerCase() === correo)) {
            throw new ApiError("Ya existe una cuenta con ese correo.", 409);
        }

        const usuario = {
            id: nextMockId++,
            nombre: datos.nombre.trim(),
            apellido: datos.apellido.trim(),
            correo,
            telefono: datos.telefono.trim(),
            password: datos.password,
            rol: "ESTUDIANTE",
        };
        mockUsers.push(usuario);

        const datosUsuario = { ...usuario };
        delete datosUsuario.password;
        return { token: "mock-token", usuario: datosUsuario };
    },
};

const real = {
    iniciarSesion: (correo, password) =>
        apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify({ correo, password }),
        }),
    registrarse: (datos) =>
        apiFetch("/auth/register", {
            method: "POST",
            body: JSON.stringify(datos),
        }),
};

export const authService = USE_MOCK ? mock : real;
