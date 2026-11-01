import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useState,
} from "react";
import { authService } from "../services/authService";

const AuthContext = createContext(null);

const leerSesion = () => {
    try {
        const token = localStorage.getItem("token");
        const usuario = JSON.parse(localStorage.getItem("usuario") ?? "null");

        if (
            !token ||
            !usuario ||
            typeof usuario.correo !== "string" ||
            typeof usuario.rol !== "string"
        ) {
            localStorage.removeItem("token");
            localStorage.removeItem("usuario");
            return null;
        }

        return usuario;
    } catch {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        return null;
    }
};

const guardarSesion = (respuesta) => {
    if (
        typeof respuesta?.token !== "string" ||
        !respuesta.token ||
        !respuesta.usuario ||
        typeof respuesta.usuario.correo !== "string" ||
        typeof respuesta.usuario.rol !== "string"
    ) {
        throw new Error("El servidor devolvió una respuesta de autenticación inválida.");
    }

    localStorage.setItem("token", respuesta.token);
    localStorage.setItem("usuario", JSON.stringify(respuesta.usuario));
    return respuesta.usuario;
};

export function AuthProvider({ children }) {
    const [user, setUser] = useState(leerSesion);

    useEffect(() => {
        const sincronizarSesion = (event) => {
            if (event.key === null || event.key === "token" || event.key === "usuario") {
                setUser(leerSesion());
            }
        };

        window.addEventListener("storage", sincronizarSesion);
        return () => window.removeEventListener("storage", sincronizarSesion);
    }, []);

    const login = useCallback(async (correo, password) => {
        const respuesta = await authService.iniciarSesion(correo, password);
        const usuario = guardarSesion(respuesta);
        setUser(usuario);
        return usuario;
    }, []);

    const register = useCallback(async (datos) => {
        const respuesta = await authService.registrarse(datos);
        const usuario = guardarSesion(respuesta);
        setUser(usuario);
        return usuario;
    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        setUser(null);
    }, []);

    const value = useMemo(
        () => ({ user, login, register, logout }),
        [user, login, register, logout]
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    return ctx;
}