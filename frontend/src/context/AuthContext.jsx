import { createContext, useContext, useMemo, useState } from "react";
import { apiFetch, ApiError } from "../services/api";
import { MENUS } from "../config/roles";

const MOCK_AUTH = import.meta.env.VITE_MOCK_AUTH === "true";
const AuthContext = createContext(null);

// Lee la sesión guardada (así no se pierde al recargar la página).
// Si el rol guardado ya no existe (por ejemplo "ADMIN"), la descarta.
const leerSesion = () => {
    try {
        const raw = localStorage.getItem("usuario");
        const usuario = raw ? JSON.parse(raw) : null;
        return usuario && MENUS[usuario.rol] ? usuario : null;
    } catch {
        return null;
    }
};

// Usuarios simulados, SOLO mientras el backend no tenga /auth/*
// Se usa "let" para poder agregar usuarios desde registerMock.
let usuariosMock = [
    { nombre: "Carlos", apellido: "Ramírez", correo: "admin@arteymovimiento.com", password: "admin123", rol: "ADMINISTRADOR" },
    { nombre: "Laura", apellido: "Gómez", correo: "profe@arteymovimiento.com", password: "profe123", rol: "PROFESOR" },
    { nombre: "María", apellido: "López", correo: "estudiante@arteymovimiento.com", password: "estudiante123", rol: "ESTUDIANTE" },
];

const loginMock = async (correo, password) => {
    await new Promise((r) => setTimeout(r, 600));
    const encontrado = usuariosMock.find(
        (u) => u.correo === correo && u.password === password
    );
    if (!encontrado) throw new ApiError("Correo o contraseña incorrectos", 401);
    return {
        token: "mock-token",
        usuario: {
            id: usuariosMock.indexOf(encontrado) + 1,
            nombre: encontrado.nombre,
            apellido: encontrado.apellido,
            correo: encontrado.correo,
            rol: encontrado.rol,
        },
    };
};

const registerMock = async ({ nombre, apellido, correo, telefono, password }) => {
    await new Promise((r) => setTimeout(r, 600));

    const existe = usuariosMock.some((u) => u.correo === correo);
    if (existe) throw new ApiError("Ya existe una cuenta con ese correo.", 409);

    const nuevo = { nombre, apellido, correo, telefono, password, rol: "ESTUDIANTE" };
    usuariosMock = [...usuariosMock, nuevo];

    return {
        token: "mock-token",
        usuario: {
            id: usuariosMock.length,
            nombre: nuevo.nombre,
            apellido: nuevo.apellido,
            correo: nuevo.correo,
            rol: nuevo.rol,
        },
    };
};

const loginReal = (correo, password) =>
    apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ correo, password }),
    });

const registerReal = (datos) =>
    apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify(datos),
    });

export function AuthProvider({ children }) {
    const [user, setUser] = useState(leerSesion);

    const login = async (correo, password) => {
        const { token, usuario } = MOCK_AUTH
            ? await loginMock(correo, password)
            : await loginReal(correo, password);

        localStorage.setItem("token", token);
        localStorage.setItem("usuario", JSON.stringify(usuario));
        setUser(usuario);
        return usuario;
    };

    const register = async (datos) => {
        const { token, usuario } = MOCK_AUTH
            ? await registerMock(datos)
            : await registerReal(datos);

        localStorage.setItem("token", token);
        localStorage.setItem("usuario", JSON.stringify(usuario));
        setUser(usuario);
        return usuario;
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        setUser(null);
    };

    const value = useMemo(() => ({ user, login, register, logout }), [user]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    return ctx;
}