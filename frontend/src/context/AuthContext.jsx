import { createContext, useContext, useMemo, useState } from "react";
import { apiFetch, ApiError } from "../services/api";

const USE_MOCK = import.meta.env.VITE_USE_MOCK === "true";
const AuthContext = createContext(null);

// Lee la sesión guardada (así no se pierde al recargar la página)
const leerSesion = () => {
    try {
        const raw = localStorage.getItem("usuario");
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
};

// Login simulado, SOLO mientras el backend no exista
const loginMock = async (correo, password) => {
    await new Promise((r) => setTimeout(r, 600));
    if (correo === "admin@arteymovimiento.com" && password === "admin123") {
        return { token: "mock-token", usuario: { id: 1, correo, rol: "ADMIN" } };
    }
    throw new ApiError("Correo o contraseña incorrectos", 401);
};

const loginReal = (correo, password) =>
    apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ correo, password }),
    });

export function AuthProvider({ children }) {
    const [user, setUser] = useState(leerSesion);

    const login = async (correo, password) => {
        const { token, usuario } = USE_MOCK
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