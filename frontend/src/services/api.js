const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";
export const SESSION_EXPIRED_EVENT = "auth:session-expired";

export function tokenVigente(token) {
    try {
        const parte = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        const { exp } = JSON.parse(atob(parte));
        return Number.isFinite(exp) && exp * 1000 > Date.now();
    } catch {
        return false;
    }
}

export class ApiError extends Error {
    constructor(message, status) {
        super(message);
        this.name = "ApiError";
        this.status = status;
    }
}

const mensajePorEstado = (status) => {
    switch (status) {
        case 400:
            return "Los datos enviados no son válidos.";
        case 401:
            return "Tu sesión expiró. Inicia sesión de nuevo.";
        case 403:
            return "No tienes permisos para hacer esta acción.";
        case 404:
            return "No se encontró el recurso solicitado.";
        case 409:
            return "Ya existe un registro con esos datos o tiene elementos asociados.";
        case 413:
            return "El archivo supera el tamaño máximo permitido.";
        case 415:
            return "El tipo de archivo no está permitido.";
        default:
            return "Error del servidor. Intenta de nuevo más tarde.";
    }
};

function tokenActual() {
    const tokenGuardado = localStorage.getItem("token");
    const token = tokenGuardado && tokenVigente(tokenGuardado) ? tokenGuardado : null;

    if (tokenGuardado && !token) {
        window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
    }

    return token;
}

async function enviar(path, options, headers, token) {
    let response;
    try {
        response = await fetch(`${API_URL}${path}`, { ...options, headers });
    } catch {
        throw new ApiError(
            "No se pudo conectar con el servidor. Revisa tu conexión.",
            0
        );
    }

    if (!response.ok) {
        let data = null;
        try {
            data = await response.json();
        } catch {
            // la respuesta de error no trae JSON
        }

        if (response.status === 401 && token) {
            window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
        }

        throw new ApiError(
            data?.message ?? mensajePorEstado(response.status),
            response.status
        );
    }

    return response;
}

export async function apiFetch(path, options = {}) {
    const token = tokenActual();

    // Con FormData el navegador define Content-Type (multipart + boundary)
    const esFormData = typeof FormData !== "undefined" && options.body instanceof FormData;

    const headers = {
        ...(!esFormData && { "Content-Type": "application/json" }),
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
    };

    const response = await enviar(path, options, headers, token);

    // 204 = éxito sin contenido (típico del DELETE)
    if (response.status === 204) return null;

    try {
        return await response.json();
    } catch {
        return null;
    }
}

// Descarga un recurso binario autenticado (el JWT va en la cabecera, nunca en la URL)
export async function apiFetchBlob(path, options = {}) {
    const token = tokenActual();

    const headers = {
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
    };

    const response = await enviar(path, options, headers, token);
    return response.blob();
}
