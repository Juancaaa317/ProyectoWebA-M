import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { apiFetch, SESSION_EXPIRED_EVENT, tokenVigente } from "../services/api";
import { MENUS } from "../config/roles";

const AuthContext = createContext(null);

// MOCK: usuarios de prueba para trabajar sin backend (VITE_MOCK_AUTH=true en el .env)
const MOCK_AUTH = import.meta.env.VITE_MOCK_AUTH === "true";
const TOKEN_MOCK = "mock-token";

const USUARIOS_MOCK = [
    { correo: "admin@arteymovimiento.com", password: "admin123", rol: "ADMINISTRADOR", nombre: "Administrador" },
    { correo: "profe@arteymovimiento.com", password: "profe123", rol: "PROFESOR", nombre: "Profesor" },
    { correo: "estudiante@arteymovimiento.com", password: "estudiante123", rol: "ESTUDIANTE", nombre: "Estudiante" },
];

const loginMock = async (correo, password) => {
    await new Promise((r) => setTimeout(r, 600));
    const encontrado = USUARIOS_MOCK.find(
        (u) => u.correo === correo && u.password === password
    );
    if (!encontrado) {
        const error = new Error("Correo o contraseña incorrectos");
        error.status = 401;
        throw error;
    }
    const usuario = {
        id: USUARIOS_MOCK.indexOf(encontrado) + 1,
        correo: encontrado.correo,
        rol: encontrado.rol,
        nombre: encontrado.nombre,
    };
    localStorage.setItem("token", TOKEN_MOCK);
    localStorage.setItem("usuario", JSON.stringify(usuario));
    return usuario;
};

// Lee la sesión guardada (así no se pierde al recargar la página).
// Si el rol guardado ya no existe (por ejemplo "ADMIN"), la descarta.
const leerSesion = () => {
    try {
        const raw = localStorage.getItem("usuario");
        const token = localStorage.getItem("token");
        const usuario = raw ? JSON.parse(raw) : null;
        if (token && (tokenVigente(token) || (MOCK_AUTH && token === TOKEN_MOCK)) && usuario && MENUS[usuario.rol]) return usuario; // MOCK: acepta el token de prueba
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
        const usuario = await (MOCK_AUTH ? loginMock : login)(correo, password); // MOCK: elige mock o backend
        setUser(usuario);
        return usuario;
    };

    // Refleja en la sesión los datos que el usuario cambia en su perfil (el nombre se ve en el menú y el saludo)
    const actualizarUsuario = useCallback((cambios) => {
        setUser((actual) => {
            const siguiente = { ...actual, ...cambios };
            localStorage.setItem("usuario", JSON.stringify(siguiente));
            return siguiente;
        });
    }, []);

    const value = useMemo(
        () => ({ user, login: iniciarSesion, register, logout, actualizarUsuario }),
        [user, logout, actualizarUsuario]
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    return ctx;
}