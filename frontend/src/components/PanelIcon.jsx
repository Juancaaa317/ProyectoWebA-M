import {
    CalendarDays,
    ClipboardCheck,
    ClipboardList,
    CreditCard,
    Globe,
    GraduationCap,
    House,
    LogOut,
    Menu,
    Music2,
    UserRound,
    UsersRound,
} from "lucide-react";

const ICONOS = {
    menu: Menu,
    inicio: House,
    programas: Music2,
    clases: CalendarDays,
    estudiantes: UsersRound,
    profesores: GraduationCap,
    inscripciones: ClipboardList,
    asistencia: ClipboardCheck,
    pagos: CreditCard,
    perfil: UserRound,
    sitio: Globe,
    salir: LogOut,
};

function PanelIcon({ name, className = "" }) {
    const Icono = ICONOS[name] ?? House;
    return <Icono className={className} aria-hidden="true" focusable="false" />;
}

export default PanelIcon;
