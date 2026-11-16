import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { apiFetch, SESSION_EXPIRED_EVENT, tokenVigente } from "../services/api";
import { MENUS } from "../config/roles";

const AuthContext = createContext(null);

// Lee la sesión guardada (así no se pierde al recargar la página).
// Si el rol guardado ya no existe (por ejemplo "ADMIN"), la descarta.
const leerSesion = () => {
    try {
        const raw = localStorage.getItem("usuario");
        const token = localStorage.getItem("token");
        const usuario = raw ? JSON.parse(raw) : null;
        if (token && tokenVigente(token) && usuario && MENUS[usuario.rol]) return usuario;
    } catch {
        // Una sesión dañada se elimina para que no deje datos inválidos guardados.
    }
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    return null;
};

const login = async (correo, password) => {
    const { token, usuario } = await apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ correo, password }),
    });

    localStorage.setItem("token", token);
    localStorage.setItem("usuario", JSON.stringify(usuario));
    return usuario;
};

const register = (datos) =>
    apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify(datos),
    });

export function AuthProvider({ children }) {
    const [user, setUser] = useState(leerSesion);

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        setUser(null);
    }, []);

    useEffect(() => {
        window.addEventListener(SESSION_EXPIRED_EVENT, logout);
        const sincronizarCierreEnOtraPestana = (event) => {
            if (event.key === "token" && event.newValue === null) logout();
        };
        window.addEventListener("storage", sincronizarCierreEnOtraPestana);
        return () => {
            window.removeEventListener(SESSION_EXPIRED_EVENT, logout);
            window.removeEventListener("storage", sincronizarCierreEnOtraPestana);
        };
    }, [logout]);

    const iniciarSesion = async (correo, password) => {
        const usuario = await login(correo, password);
        setUser(usuario);
        return usuario;
    };

    const value = useMemo(
        () => ({ user, login: iniciarSesion, register, logout }),
        [user, logout]
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    return ctx;
}
