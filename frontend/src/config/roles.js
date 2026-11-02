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

const perfil = { path: "perfil", label: "Mi perfil", icon: "perfil" };
const resumen = { path: "", label: "Resumen", icon: "inicio" };

export const MENUS = {
    ADMINISTRADOR: [
        resumen,
        { path: "programas", label: "Programas", icon: "programas" },
        {
            path: "clases",
            label: "Clases y horarios",
            icon: "clases",
            descripcion:
                "Crear y editar clases, asignar profesor y programa, y definir sus horarios.",
        },
        {
            path: "estudiantes",
            label: "Estudiantes",
            icon: "estudiantes",
            descripcion: "Lista de estudiantes y búsqueda por nombre, correo o tarjeta de identidad.",
        },
        {
            path: "profesores",
            label: "Profesores",
            icon: "profesores",
            descripcion: "Registrar profesores y consultar el listado por nombre o correo.",
        },
        {
            path: "inscripciones",
            label: "Inscripciones",
            icon: "inscripciones",
            descripcion:
                "Matricular estudiantes en clases y cambiar el estado de cada inscripción.",
        },
        {
            path: "asistencia",
            label: "Asistencia",
            icon: "asistencia",
            descripcion: "Registro general de asistencias por clase y por estudiante.",
        },
        {
            path: "pagos",
            label: "Pagos",
            icon: "pagos",
            descripcion:
                "Revisar los comprobantes subidos y aprobarlos o rechazarlos.",
        },
        perfil,
    ],

    PROFESOR: [
        resumen,
        {
            path: "clases",
            label: "Mis clases",
            icon: "clases",
            descripcion: "Consulta clases y horarios; el servidor verifica la asignación al tomar asistencia.",
        },
        {
            path: "horario",
            label: "Mi horario",
            icon: "clases",
            descripcion: "Horario semanal de tus clases.",
        },
        {
            path: "asistencia",
            label: "Tomar asistencia",
            icon: "asistencia",
            descripcion:
                "Registrar presente, ausente o justificado en cada sesión de clase.",
        },
        perfil,
    ],

    ESTUDIANTE: [
        resumen,
        {
            path: "inscripciones",
            label: "Mis inscripciones",
            icon: "inscripciones",
            descripcion:
                "Inscríbete a clases y consulta el estado de tus inscripciones.",
        },
        {
            path: "horario",
            label: "Mi horario",
            icon: "clases",
            descripcion: "Horario de las clases en las que estás inscrito.",
        },
        {
            path: "asistencia",
            label: "Mi asistencia",
            icon: "asistencia",
            descripcion: "Historial de asistencia por clase.",
        },
        {
            path: "pagos",
            label: "Mis pagos",
            icon: "pagos",
            descripcion:
                "Sube tus comprobantes de pago y consulta si fueron aprobados.",
        },
        perfil,
    ],
};

export const rutaDe = (item) => (item.path ? `/panel/${item.path}` : "/panel");
