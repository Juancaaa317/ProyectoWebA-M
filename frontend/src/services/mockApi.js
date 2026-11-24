// ============================================================
// API SIMULADA — datos locales para trabajar SIN backend.
// Se activa con VITE_MOCK_AUTH=true (o VITE_MOCK_PROGRAMAS=true) en el .env
// y responde con la misma forma que la API real (ver backend/API.md).
// Los cambios se guardan en localStorage ("am-mock-db"); para reiniciar
// los datos basta con borrar esa clave.
// ============================================================
import { ApiError } from "./api";

export const MOCK_ACTIVO =
    import.meta.env.VITE_MOCK_AUTH === "true" ||
    import.meta.env.VITE_MOCK_PROGRAMAS === "true";

const CLAVE_DB = "am-mock-db";
const retraso = (ms = 250) => new Promise((r) => setTimeout(r, ms));
const copiar = (valor) => JSON.parse(JSON.stringify(valor));
const hoy = () => new Date().toISOString().slice(0, 10);

const proximaFecha = (diasDesdeHoy) => {
    const d = new Date();
    d.setDate(d.getDate() + diasDesdeHoy);
    return d.toISOString().slice(0, 10);
};

function datosIniciales() {
    const periodo = hoy().slice(0, 7);
    return {
        programas: [
            { idPrograma: 1, nombrePrograma: "Ballet", descripcion: "Técnica clásica, postura y elegancia. Para niñas, jóvenes y adultas de todos los niveles.", estadoPrograma: "ACTIVO" },
            { idPrograma: 2, nombrePrograma: "Danza acrobática", descripcion: "Flexibilidad, fuerza y acrobacia combinadas con danza.", estadoPrograma: "ACTIVO" },
            { idPrograma: 3, nombrePrograma: "Danza árabe", descripcion: "Ritmo, expresión y movimiento fluido en un ambiente alegre.", estadoPrograma: "ACTIVO" },
            { idPrograma: 4, nombrePrograma: "Clase personalizada", descripcion: "Sesiones a tu medida, con horario y objetivos acordados con el profesor.", estadoPrograma: "INACTIVO" },
        ],
        profesores: [
            { idProfesor: 1, idUsuario: 2, nombre: "Profesor", correo: "profe@arteymovimiento.com", telefono: "3001234567", areaDocencia: "DOCENTE" },
            { idProfesor: 2, idUsuario: 11, nombre: "María Gómez", correo: "maria.gomez@arteymovimiento.com", telefono: "3109876543", areaDocencia: "PROFESIONAL" },
        ],
        estudiantes: [
            { idEstudiante: 1, idUsuario: 3, nombre: "Estudiante", tarjetaIdentidad: "1002003001", correo: "estudiante@arteymovimiento.com", telefono: "3201112233" },
            { idEstudiante: 2, idUsuario: 12, nombre: "Valentina Ríos", tarjetaIdentidad: "1002003002", correo: "valentina.rios@correo.com", telefono: "3154445566" },
            { idEstudiante: 3, idUsuario: 13, nombre: "Mateo Pérez", tarjetaIdentidad: "1002003003", correo: "mateo.perez@correo.com", telefono: "3187778899" },
        ],
        clases: [
            { idClase: 1, idProfesor: 1, idPrograma: 1, nombre: "Ballet infantil", fechaInicio: proximaFecha(-60), fechaFinalizacion: proximaFecha(120), cupoMaximo: 15, estadoClase: "EN_CURSO" },
            { idClase: 2, idProfesor: 2, idPrograma: 2, nombre: "Acrobática juvenil", fechaInicio: proximaFecha(-30), fechaFinalizacion: proximaFecha(150), cupoMaximo: 12, estadoClase: "EN_CURSO" },
            { idClase: 3, idProfesor: 1, idPrograma: 3, nombre: "Danza árabe adultas", fechaInicio: proximaFecha(10), fechaFinalizacion: proximaFecha(190), cupoMaximo: 10, estadoClase: "PROGRAMADA" },
        ],
        horarios: [
            { idHorario: 1, idClase: 1, diaSemana: "LUNES", horaInicio: "16:00:00", horaFin: "17:00:00" },
            { idHorario: 2, idClase: 1, diaSemana: "MIERCOLES", horaInicio: "16:00:00", horaFin: "17:00:00" },
            { idHorario: 3, idClase: 2, diaSemana: "MARTES", horaInicio: "17:30:00", horaFin: "19:00:00" },
            { idHorario: 4, idClase: 3, diaSemana: "SABADO", horaInicio: "10:00:00", horaFin: "11:30:00" },
        ],
        inscripciones: [
            { idInscripcion: 1, idEstudiante: 1, idClase: 1, fechaInscripcion: proximaFecha(-45), estadoInscripcion: "ACTIVA" },
            { idInscripcion: 2, idEstudiante: 1, idClase: 2, fechaInscripcion: proximaFecha(-2), estadoInscripcion: "PENDIENTE" },
            { idInscripcion: 3, idEstudiante: 2, idClase: 1, fechaInscripcion: proximaFecha(-40), estadoInscripcion: "ACTIVA" },
            { idInscripcion: 4, idEstudiante: 3, idClase: 3, fechaInscripcion: proximaFecha(-1), estadoInscripcion: "PENDIENTE" },
        ],
        sesiones: [
            { idSesion: 1, idClase: 1, idHorario: 1, fecha: proximaFecha(-14), estadoSesion: "REALIZADA" },
            { idSesion: 2, idClase: 1, idHorario: 2, fecha: proximaFecha(-7), estadoSesion: "REALIZADA" },
            { idSesion: 3, idClase: 1, idHorario: 1, fecha: proximaFecha(7), estadoSesion: "PROGRAMADA" },
        ],
        asistencias: [
            { idAsistencia: 1, idSesion: 1, idInscripcion: 1, estado: "PRESENTE", observaciones: "" },
            { idAsistencia: 2, idSesion: 1, idInscripcion: 3, estado: "AUSENTE", observaciones: "Avisó por enfermedad" },
            { idAsistencia: 3, idSesion: 2, idInscripcion: 1, estado: "PRESENTE", observaciones: "" },
        ],
        pagos: [
            { idPago: 1, idEstudiante: 1, monto: 120000, periodo, fechaPago: proximaFecha(-3), estado: "PENDIENTE", observacion: null, idArchivo: 1, nombreArchivo: "comprobante-1.png" },
            { idPago: 2, idEstudiante: 1, monto: 120000, periodo: proximaFecha(-31).slice(0, 7), fechaPago: proximaFecha(-33), estado: "APROBADO", observacion: null, idArchivo: 2, nombreArchivo: "comprobante-2.png" },
            { idPago: 3, idEstudiante: 2, monto: 95000, periodo, fechaPago: proximaFecha(-5), estado: "RECHAZADO", observacion: "El comprobante no permite verificar el valor pagado.", idArchivo: 3, nombreArchivo: "comprobante-3.png" },
            { idPago: 4, idEstudiante: 2, monto: 95000, periodo: proximaFecha(-31).slice(0, 7), fechaPago: proximaFecha(-34), estado: "APROBADO", observacion: null, idArchivo: 4, nombreArchivo: "comprobante-4.png" },
        ],
        // Perfiles editables por correo
        perfiles: {},
        sig: { programa: 5, clase: 4, horario: 5, inscripcion: 5, sesion: 4, asistencia: 4, pago: 5, profesor: 3, estudiante: 4, usuario: 20 },
    };
}

let db;
function cargarDb() {
    if (db) return db;
    try {
        const guardado = localStorage.getItem(CLAVE_DB);
        db = guardado ? JSON.parse(guardado) : datosIniciales();
    } catch {
        db = datosIniciales();
    }
    return db;
}
function guardarDb() {
    try {
        localStorage.setItem(CLAVE_DB, JSON.stringify(db));
    } catch {
        // sin espacio o sin localStorage: los datos quedan solo en memoria
    }
}

// ---------- Utilidades de relación ----------
const nombreProfesor = (d, id) => d.profesores.find((p) => p.idProfesor === id)?.nombre ?? "";
const nombreEstudiante = (d, id) => d.estudiantes.find((e) => e.idEstudiante === id)?.nombre ?? "";
const programa = (d, id) => d.programas.find((p) => p.idPrograma === id);

const clase = (d, c) => ({
    ...c,
    nombreProfesor: nombreProfesor(d, c.idProfesor),
    nombrePrograma: programa(d, c.idPrograma)?.nombrePrograma ?? "",
});

const horario = (d, h) => {
    const c = d.clases.find((x) => x.idClase === h.idClase);
    return { ...h, nombreClase: c?.nombre ?? "", nombrePrograma: c ? programa(d, c.idPrograma)?.nombrePrograma ?? "" : "" };
};

const inscripcion = (d, i) => {
    const c = d.clases.find((x) => x.idClase === i.idClase);
    return {
        ...i,
        nombreEstudiante: nombreEstudiante(d, i.idEstudiante),
        nombreClase: c?.nombre ?? "",
        nombrePrograma: c ? programa(d, c.idPrograma)?.nombrePrograma ?? "" : "",
    };
};

const sesion = (d, s) => {
    const c = d.clases.find((x) => x.idClase === s.idClase);
    const h = d.horarios.find((x) => x.idHorario === s.idHorario);
    return {
        idSesion: s.idSesion,
        idClase: s.idClase,
        nombreClase: c?.nombre ?? "",
        nombrePrograma: c ? programa(d, c.idPrograma)?.nombrePrograma ?? "" : "",
        fecha: s.fecha,
        horaInicio: h?.horaInicio ?? "00:00:00",
        horaFin: h?.horaFin ?? "00:00:00",
        estadoSesion: s.estadoSesion,
    };
};

const pago = (d, p) => ({ ...p, nombreEstudiante: nombreEstudiante(d, p.idEstudiante) });

// Usuario de la sesión actual (guardado por el login simulado)
function usuarioActual() {
    try {
        return JSON.parse(localStorage.getItem("usuario")) ?? null;
    } catch {
        return null;
    }
}

// Estudiante/profesor ligado al usuario de sesión (por correo)
const estudianteDe = (d, u) => d.estudiantes.find((e) => e.correo === u?.correo);
const profesorDe = (d, u) => d.profesores.find((p) => p.correo === u?.correo);

function leerCuerpo(options) {
    const cuerpo = options?.body;
    if (!cuerpo) return {};
    if (typeof FormData !== "undefined" && cuerpo instanceof FormData) return Object.fromEntries(cuerpo.entries());
    try {
        return JSON.parse(cuerpo);
    } catch {
        return {};
    }
}

const exigir = (condicion, mensaje, estado = 400) => {
    if (!condicion) throw new ApiError(mensaje, estado);
};

// ---------- Rutas ----------
// Cada ruta: [método, expresión, manejador(d, params, cuerpo, consulta, usuario)]
const RUTAS = [
    // Programas
    ["GET", /^\/programas$/, (d) => d.programas],
    ["GET", /^\/programas\/estado\/(\w+)$/, (d, [estado]) => d.programas.filter((p) => p.estadoPrograma === estado)],
    ["GET", /^\/programas\/(\d+)$/, (d, [id]) => {
        const p = programa(d, +id);
        exigir(p, "No se encontró el programa.", 404);
        return p;
    }],
    ["POST", /^\/programas$/, (d, _p, b) => {
        exigir(b.nombrePrograma?.trim(), "El nombre del programa es obligatorio.");
        const nuevo = { idPrograma: d.sig.programa++, nombrePrograma: b.nombrePrograma.trim(), descripcion: b.descripcion ?? "", estadoPrograma: b.estadoPrograma ?? "ACTIVO" };
        d.programas.push(nuevo);
        return nuevo;
    }],
    ["PUT", /^\/programas\/(\d+)$/, (d, [id], b) => {
        const p = programa(d, +id);
        exigir(p, "No se encontró el programa.", 404);
        exigir(b.nombrePrograma?.trim(), "El nombre del programa es obligatorio.");
        Object.assign(p, { nombrePrograma: b.nombrePrograma.trim(), descripcion: b.descripcion ?? "", estadoPrograma: b.estadoPrograma ?? p.estadoPrograma });
        return p;
    }],
    ["DELETE", /^\/programas\/(\d+)$/, (d, [id]) => {
        exigir(!d.clases.some((c) => c.idPrograma === +id), "El programa tiene clases asociadas.", 409);
        d.programas = d.programas.filter((p) => p.idPrograma !== +id);
        return null;
    }],

    // Clases
    ["GET", /^\/clases$/, (d) => d.clases.map((c) => clase(d, c))],
    ["GET", /^\/clases\/mias$/, (d, _p, _b, _q, u) => {
        const prof = profesorDe(d, u);
        return d.clases.filter((c) => c.idProfesor === prof?.idProfesor)
            .sort((a, b) => a.nombre.localeCompare(b.nombre))
            .map((c) => clase(d, c));
    }],
    ["GET", /^\/clases\/(\d+)$/, (d, [id]) => {
        const c = d.clases.find((x) => x.idClase === +id);
        exigir(c, "No se encontró la clase.", 404);
        return clase(d, c);
    }],
    ["POST", /^\/clases$/, (d, _p, b) => {
        exigir(b.nombre?.trim(), "El nombre de la clase es obligatorio.");
        const nueva = { idClase: d.sig.clase++, idProfesor: +b.idProfesor, idPrograma: +b.idPrograma, nombre: b.nombre.trim(), fechaInicio: b.fechaInicio ?? null, fechaFinalizacion: b.fechaFinalizacion ?? null, cupoMaximo: +b.cupoMaximo, estadoClase: b.estadoClase ?? "PROGRAMADA" };
        d.clases.push(nueva);
        return clase(d, nueva);
    }],
    ["PUT", /^\/clases\/(\d+)$/, (d, [id], b) => {
        const c = d.clases.find((x) => x.idClase === +id);
        exigir(c, "No se encontró la clase.", 404);
        Object.assign(c, { idProfesor: +b.idProfesor, idPrograma: +b.idPrograma, nombre: b.nombre?.trim() || c.nombre, fechaInicio: b.fechaInicio ?? null, fechaFinalizacion: b.fechaFinalizacion ?? null, cupoMaximo: +b.cupoMaximo, estadoClase: b.estadoClase ?? c.estadoClase });
        return clase(d, c);
    }],
    ["DELETE", /^\/clases\/(\d+)$/, (d, [id]) => {
        exigir(!d.inscripciones.some((i) => i.idClase === +id), "La clase tiene inscripciones asociadas.", 409);
        d.clases = d.clases.filter((c) => c.idClase !== +id);
        d.horarios = d.horarios.filter((h) => h.idClase !== +id);
        d.sesiones = d.sesiones.filter((s) => s.idClase !== +id);
        return null;
    }],

    // Horarios
    ["GET", /^\/horarios$/, (d) => d.horarios.map((h) => horario(d, h))],
    ["GET", /^\/horarios\/clase\/(\d+)$/, (d, [id]) => d.horarios.filter((h) => h.idClase === +id).map((h) => horario(d, h))],
    ["POST", /^\/horarios$/, (d, _p, b) => {
        exigir(b.horaFin > b.horaInicio, "La hora de fin debe ser posterior a la de inicio.");
        const nuevo = { idHorario: d.sig.horario++, idClase: +b.idClase, diaSemana: b.diaSemana, horaInicio: b.horaInicio, horaFin: b.horaFin };
        d.horarios.push(nuevo);
        return horario(d, nuevo);
    }],
    ["PUT", /^\/horarios\/(\d+)$/, (d, [id], b) => {
        const h = d.horarios.find((x) => x.idHorario === +id);
        exigir(h, "No se encontró el horario.", 404);
        Object.assign(h, { idClase: +b.idClase, diaSemana: b.diaSemana, horaInicio: b.horaInicio, horaFin: b.horaFin });
        return horario(d, h);
    }],
    ["DELETE", /^\/horarios\/(\d+)$/, (d, [id]) => {
        d.horarios = d.horarios.filter((h) => h.idHorario !== +id);
        return null;
    }],

    // Sesiones
    ["GET", /^\/sesiones\/clase\/(\d+)$/, (d, [id]) => d.sesiones.filter((s) => s.idClase === +id).map((s) => sesion(d, s))],
    ["POST", /^\/sesiones$/, (d, _p, b) => {
        const h = d.horarios.find((x) => x.idHorario === +b.idHorario);
        exigir(h, "El horario indicado no existe.", 404);
        exigir(b.fecha, "La fecha es obligatoria.");
        const nueva = { idSesion: d.sig.sesion++, idClase: h.idClase, idHorario: h.idHorario, fecha: b.fecha, estadoSesion: "PROGRAMADA" };
        d.sesiones.push(nueva);
        return sesion(d, nueva);
    }],
    ["PATCH", /^\/sesiones\/(\d+)\/estado$/, (d, [id], b) => {
        const s = d.sesiones.find((x) => x.idSesion === +id);
        exigir(s, "No se encontró la sesión.", 404);
        s.estadoSesion = b.estadoSesion;
        return sesion(d, s);
    }],

    // Asistencias
    ["GET", /^\/asistencias\/mias$/, (d, _p, _b, _q, u) => {
        const est = estudianteDe(d, u);
        const mias = d.inscripciones.filter((i) => i.idEstudiante === est?.idEstudiante);
        return d.asistencias
            .filter((a) => mias.some((i) => i.idInscripcion === a.idInscripcion))
            .map((a) => {
                const s = sesion(d, d.sesiones.find((x) => x.idSesion === a.idSesion));
                return { idAsistencia: a.idAsistencia, idClase: s.idClase, nombreClase: s.nombreClase, nombrePrograma: s.nombrePrograma, fecha: s.fecha, horaInicio: s.horaInicio, horaFin: s.horaFin, estado: a.estado, observaciones: a.observaciones };
            });
    }],
    ["GET", /^\/sesiones\/(\d+)\/asistencias$/, (d, [id]) => {
        const s = d.sesiones.find((x) => x.idSesion === +id);
        exigir(s, "No se encontró la sesión.", 404);
        return d.inscripciones
            .filter((i) => i.idClase === s.idClase && i.estadoInscripcion === "ACTIVA")
            .map((i) => {
                const reg = d.asistencias.find((a) => a.idSesion === s.idSesion && a.idInscripcion === i.idInscripcion);
                return { idInscripcion: i.idInscripcion, idEstudiante: i.idEstudiante, nombreEstudiante: nombreEstudiante(d, i.idEstudiante), estado: reg?.estado ?? null, observaciones: reg?.observaciones ?? "" };
            });
    }],
    ["PUT", /^\/sesiones\/(\d+)\/asistencias\/(\d+)$/, (d, [idSesion, idInsc], b) => {
        exigir(["PRESENTE", "AUSENTE", "JUSTIFICADO"].includes(b.estado), "El estado de asistencia no es válido.");
        let reg = d.asistencias.find((a) => a.idSesion === +idSesion && a.idInscripcion === +idInsc);
        if (!reg) {
            reg = { idAsistencia: d.sig.asistencia++, idSesion: +idSesion, idInscripcion: +idInsc };
            d.asistencias.push(reg);
        }
        reg.estado = b.estado;
        reg.observaciones = b.observaciones ?? "";
        return reg;
    }],

    // Inscripciones
    ["GET", /^\/inscripciones\/mias$/, (d, _p, _b, _q, u) => {
        const est = estudianteDe(d, u);
        return d.inscripciones.filter((i) => i.idEstudiante === est?.idEstudiante).map((i) => inscripcion(d, i));
    }],
    ["GET", /^\/inscripciones$/, (d) => d.inscripciones.map((i) => inscripcion(d, i))],
    ["POST", /^\/inscripciones$/, (d, _p, b, _q, u) => {
        const est = estudianteDe(d, u);
        exigir(est, "Solo los estudiantes pueden solicitar una inscripción.", 403);
        const c = d.clases.find((x) => x.idClase === +b.idClase);
        exigir(c, "La clase no existe.", 404);
        exigir(!d.inscripciones.some((i) => i.idClase === c.idClase && i.idEstudiante === est.idEstudiante && i.estadoInscripcion !== "CANCELADA"), "Ya tienes una inscripción en esta clase.", 409);
        const activas = d.inscripciones.filter((i) => i.idClase === c.idClase && i.estadoInscripcion === "ACTIVA").length;
        exigir(activas < c.cupoMaximo, "La clase no tiene cupos disponibles.", 409);
        const nueva = { idInscripcion: d.sig.inscripcion++, idEstudiante: est.idEstudiante, idClase: c.idClase, fechaInscripcion: hoy(), estadoInscripcion: "PENDIENTE" };
        d.inscripciones.push(nueva);
        return inscripcion(d, nueva);
    }],
    ["PATCH", /^\/inscripciones\/(\d+)\/estado$/, (d, [id], b) => {
        const i = d.inscripciones.find((x) => x.idInscripcion === +id);
        exigir(i, "No se encontró la inscripción.", 404);
        i.estadoInscripcion = b.estadoInscripcion;
        return inscripcion(d, i);
    }],

    // Usuarios
    ["GET", /^\/usuarios\/profesores$/, (d, _p, _b, q) => filtrar(d.profesores, q.get("buscar"))],
    ["GET", /^\/usuarios\/estudiantes$/, (d, _p, _b, q) => filtrar(d.estudiantes, q.get("buscar"))],
    ["POST", /^\/usuarios\/profesores$/, (d, _p, b) => {
        exigir(b.nombre?.trim() && b.correo?.trim(), "El nombre y el correo son obligatorios.");
        exigir(b.contrasena && b.contrasena === b.confirmarContrasena, "Las contraseñas no coinciden.");
        exigir(!d.profesores.some((p) => p.correo.toLowerCase() === b.correo.toLowerCase()), "Ya existe un usuario con ese correo.", 409);
        const nuevo = { idProfesor: d.sig.profesor++, idUsuario: d.sig.usuario++, nombre: b.nombre.trim(), correo: b.correo.trim(), telefono: b.telefono ?? "", areaDocencia: b.areaDocencia ?? "DOCENTE" };
        d.profesores.push(nuevo);
        return { idUsuario: nuevo.idUsuario, idProfesor: nuevo.idProfesor, nombre: nuevo.nombre, correo: nuevo.correo, areaDocencia: nuevo.areaDocencia, rol: "PROFESOR" };
    }],
    ["GET", /^\/usuarios\/me$/, (d, _p, _b, _q, u) => perfilDe(d, u)],
    ["PUT", /^\/usuarios\/me$/, (d, _p, b, _q, u) => {
        exigir(b.nombre?.trim(), "El nombre es obligatorio.");
        d.perfiles[u.correo] = { ...(d.perfiles[u.correo] ?? {}), nombre: b.nombre.trim(), telefono: b.telefono ?? "" };
        const est = estudianteDe(d, u);
        const prof = profesorDe(d, u);
        if (est) Object.assign(est, { nombre: b.nombre.trim(), telefono: b.telefono ?? "" });
        if (prof) Object.assign(prof, { nombre: b.nombre.trim(), telefono: b.telefono ?? "" });
        return perfilDe(d, u);
    }],
    ["PUT", /^\/usuarios\/me\/contrasena$/, (_d, _p, b) => {
        exigir(b.contrasenaNueva ?? b.nuevaContrasena ?? b.contrasena ?? true, "La contraseña no es válida.");
        return null;
    }],

    // Pagos
    ["GET", /^\/pagos\/mios$/, (d, _p, _b, _q, u) => {
        const est = estudianteDe(d, u);
        return d.pagos.filter((p) => p.idEstudiante === est?.idEstudiante).map((p) => pago(d, p));
    }],
    ["GET", /^\/pagos$/, (d, _p, _b, q) => {
        const estado = q.get("estado");
        const periodo = q.get("periodo");
        return d.pagos.filter((p) => (!estado || p.estado === estado) && (!periodo || p.periodo === periodo)).map((p) => pago(d, p));
    }],
    ["POST", /^\/pagos$/, (d, _p, b, _q, u) => {
        const est = estudianteDe(d, u);
        exigir(est, "Solo los estudiantes pueden registrar pagos.", 403);
        exigir(Number(b.monto) > 0, "El monto debe ser mayor que cero.");
        exigir(b.archivo, "Debes adjuntar el comprobante.");
        const id = d.sig.pago++;
        const nuevo = { idPago: id, idEstudiante: est.idEstudiante, monto: Number(b.monto), periodo: b.periodo || hoy().slice(0, 7), fechaPago: hoy(), estado: "PENDIENTE", observacion: null, idArchivo: 100 + id, nombreArchivo: b.archivo?.name || `comprobante-${id}.png` };
        d.pagos.push(nuevo);
        return pago(d, nuevo);
    }],
    ["PATCH", /^\/pagos\/(\d+)\/estado$/, (d, [id], b) => {
        const p = d.pagos.find((x) => x.idPago === +id);
        exigir(p, "No se encontró el pago.", 404);
        exigir(p.estado === "PENDIENTE", "Solo se pueden resolver pagos pendientes.", 409);
        exigir(b.estado !== "RECHAZADO" || b.observacion, "Debes indicar el motivo del rechazo.");
        p.estado = b.estado;
        p.observacion = b.observacion ?? null;
        return pago(d, p);
    }],

    // Registro de estudiantes
    ["POST", /^\/auth\/register$/, (d, _p, b) => {
        exigir(b.correo && b.nombre, "El nombre y el correo son obligatorios.");
        exigir(!d.estudiantes.some((e) => e.correo.toLowerCase() === b.correo.toLowerCase()), "Ya existe una cuenta con ese correo.", 409);
        const nuevo = { idEstudiante: d.sig.estudiante++, idUsuario: d.sig.usuario++, nombre: b.nombre.trim(), tarjetaIdentidad: b.tarjetaIdentidad ?? "", correo: b.correo.trim(), telefono: b.telefono ?? "" };
        d.estudiantes.push(nuevo);
        return { idUsuario: nuevo.idUsuario, idEstudiante: nuevo.idEstudiante, nombre: nuevo.nombre, correo: nuevo.correo, rol: "ESTUDIANTE" };
    }],
];

function filtrar(lista, buscar) {
    const q = (buscar ?? "").trim().toLowerCase();
    if (!q) return lista;
    return lista.filter((x) => [x.nombre, x.correo, x.tarjetaIdentidad].some((v) => v?.toLowerCase().includes(q)));
}

function perfilDe(d, u) {
    const est = estudianteDe(d, u);
    const prof = profesorDe(d, u);
    const extra = d.perfiles[u?.correo] ?? {};
    return {
        id: u?.id ?? 0,
        nombre: extra.nombre ?? est?.nombre ?? prof?.nombre ?? u?.nombre ?? "",
        correo: u?.correo ?? "",
        telefono: extra.telefono ?? est?.telefono ?? prof?.telefono ?? "",
        rol: u?.rol ?? "ESTUDIANTE",
        tarjetaIdentidad: est?.tarjetaIdentidad ?? null,
        fechaNacimiento: est ? "2012-05-14" : null,
        areaDocencia: prof?.areaDocencia ?? null,
    };
}

// ---------- Entrada pública ----------
export async function mockRequest(path, options = {}) {
    await retraso();
    const d = cargarDb();
    const [ruta, consulta = ""] = path.split("?");
    const metodo = (options.method ?? "GET").toUpperCase();
    const cuerpo = leerCuerpo(options);
    const params = new URLSearchParams(consulta);
    const usuario = usuarioActual();

    for (const [m, patron, manejador] of RUTAS) {
        if (m !== metodo) continue;
        const coincide = ruta.match(patron);
        if (!coincide) continue;
        const resultado = manejador(d, coincide.slice(1), cuerpo, params, usuario);
        guardarDb();
        return resultado === undefined ? null : copiar(resultado);
    }
    throw new ApiError("No se encontró el recurso solicitado.", 404);
}

// Comprobante de ejemplo: una imagen PNG generada al vuelo
export async function mockComprobante(idArchivo) {
    await retraso(150);
    const lienzo = document.createElement("canvas");
    lienzo.width = 640;
    lienzo.height = 360;
    const ctx = lienzo.getContext("2d");
    ctx.fillStyle = "#f2efe9";
    ctx.fillRect(0, 0, lienzo.width, lienzo.height);
    ctx.strokeStyle = "#b39659";
    ctx.lineWidth = 4;
    ctx.strokeRect(14, 14, lienzo.width - 28, lienzo.height - 28);
    ctx.fillStyle = "#26292e";
    ctx.font = "bold 30px sans-serif";
    ctx.fillText("Comprobante de ejemplo", 60, 150);
    ctx.font = "20px sans-serif";
    ctx.fillStyle = "#8c929b";
    ctx.fillText(`Archivo #${idArchivo} · Arte & Movimiento (datos simulados)`, 60, 200);
    return new Promise((resolver) => lienzo.toBlob((blob) => resolver(blob), "image/png"));
}
