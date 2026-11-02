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

// Login simulado, SOLO mientras el backend no tenga /auth/login
const USUARIOS_MOCK = [
    { correo: "admin@arteymovimiento.com", password: "admin123", rol: "ADMINISTRADOR" },
    { correo: "profe@arteymovimiento.com", password: "profe123", rol: "PROFESOR" },
    { correo: "estudiante@arteymovimiento.com", password: "estudiante123", rol: "ESTUDIANTE" },
];

const loginMock = async (correo, password) => {
    await new Promise((r) => setTimeout(r, 600));
    const encontrado = USUARIOS_MOCK.find(
        (u) => u.correo === correo && u.password === password
    );
    if (!encontrado) throw new ApiError("Correo o contraseña incorrectos", 401);
    return {
        token: "mock-token",
        usuario: {
            id: USUARIOS_MOCK.indexOf(encontrado) + 1,
            correo: encontrado.correo,
            rol: encontrado.rol,
        },
    };
};

const loginReal = (correo, password) =>
    apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ correo, password }),
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

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        setUser(null);
    };

    const value = useMemo(() => ({ user, login, logout }), [user]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    return ctx;
}