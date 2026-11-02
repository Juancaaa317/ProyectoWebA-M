export const ROLES = {
    ADMIN: "ADMINISTRADOR",
    PROFESOR: "PROFESOR",
    ESTUDIANTE: "ESTUDIANTE",
};

export const ROL_INFO = {
    ADMINISTRADOR: {
        titulo: "Administración",
        bienvenida:
            "Gestiona programas, clases, usuarios, asistencia y pagos de la academia.",
    },
    PROFESOR: {
        titulo: "Profesor",
        bienvenida:
            "Consulta tus clases y horarios, y registra la asistencia de tus alumnos.",
    },
    ESTUDIANTE: {
        titulo: "Estudiante",
        bienvenida:
            "Inscríbete a clases, revisa tu horario y asistencia, y sube tus comprobantes de pago.",
    },
};

const perfil = { path: "perfil", label: "Mi perfil", icon: "👤" };
const resumen = { path: "", label: "Resumen", icon: "📊" };

// pronto: true = sección sin construir todavía (muestra pantalla "En construcción")
// datos = entidades del diagrama que usará la sección
export const MENUS = {
    ADMINISTRADOR: [
        resumen,
        { path: "programas", label: "Programas", icon: "🩰" },
        {
            path: "clases",
            label: "Clases y horarios",
            icon: "🗓️",
            pronto: true,
            descripcion:
                "Crear y editar clases, asignar profesor y programa, y definir sus horarios.",
            datos: ["Clase", "Horario", "Profesor", "Programa"],
        },
        {
            path: "estudiantes",
            label: "Estudiantes",
            icon: "🎓",
            pronto: true,
            descripcion: "Lista de estudiantes con sus acudientes e inscripciones.",
            datos: ["Estudiante", "Acudiente", "EstudianteAcudiente"],
        },
        {
            path: "profesores",
            label: "Profesores",
            icon: "🧑‍🏫",
            pronto: true,
            descripcion: "Registrar profesores y ver sus clases asignadas.",
            datos: ["Profesor", "Usuario", "Clase"],
        },
        {
            path: "inscripciones",
            label: "Inscripciones",
            icon: "📝",
            pronto: true,
            descripcion:
                "Matricular estudiantes en clases y cambiar el estado de cada inscripción.",
            datos: ["Inscripcion", "Clase", "Estudiante"],
        },
        {
            path: "asistencia",
            label: "Asistencia",
            icon: "✅",
            pronto: true,
            descripcion: "Registro general de asistencias por clase y por estudiante.",
            datos: ["Asistencia", "SesionClase", "Inscripcion"],
        },
        {
            path: "pagos",
            label: "Pagos",
            icon: "💳",
            pronto: true,
            descripcion:
                "Revisar los comprobantes subidos y aprobarlos o rechazarlos.",
            datos: ["Pago", "Archivo", "Estudiante"],
        },
        perfil,
    ],

    PROFESOR: [
        resumen,
        {
            path: "clases",
            label: "Mis clases",
            icon: "🩰",
            pronto: true,
            descripcion: "Clases que tienes asignadas y los estudiantes inscritos.",
            datos: ["Clase", "Inscripcion"],
        },
        {
            path: "horario",
            label: "Mi horario",
            icon: "🗓️",
            pronto: true,
            descripcion: "Horario semanal de tus clases.",
            datos: ["Horario", "Clase"],
        },
        {
            path: "asistencia",
            label: "Tomar asistencia",
            icon: "✅",
            pronto: true,
            descripcion:
                "Registrar presente, ausente o justificado en cada sesión de clase.",
            datos: ["SesionClase", "Asistencia"],
        },
        perfil,
    ],

    ESTUDIANTE: [
        resumen,
        {
            path: "inscripciones",
            label: "Mis inscripciones",
            icon: "📝",
            pronto: true,
            descripcion:
                "Inscríbete a clases y consulta el estado de tus inscripciones.",
            datos: ["Inscripcion", "Clase"],
        },
        {
            path: "horario",
            label: "Mi horario",
            icon: "🗓️",
            pronto: true,
            descripcion: "Horario de las clases en las que estás inscrito.",
            datos: ["Horario", "Inscripcion"],
        },
        {
            path: "asistencia",
            label: "Mi asistencia",
            icon: "✅",
            pronto: true,
            descripcion: "Historial de asistencia por clase.",
            datos: ["Asistencia", "Inscripcion"],
        },
        {
            path: "pagos",
            label: "Mis pagos",
            icon: "💳",
            pronto: true,
            descripcion:
                "Sube tus comprobantes de pago y consulta si fueron aprobados.",
            datos: ["Pago", "Archivo"],
        },
        perfil,
    ],
};

export const rutaDe = (item) => (item.path ? `/panel/${item.path}` : "/panel");