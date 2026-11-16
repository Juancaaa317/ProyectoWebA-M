import { Navigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { MENUS } from "../config/roles";
import PanelIcon from "../components/PanelIcon";

function SeccionPendiente() {
    const { seccion } = useParams();
    const { user } = useAuth();

    const item = (MENUS[user.rol] ?? []).find((i) => i.path === seccion);

    // Si la sección no existe para este rol, vuelve al resumen
    if (!item) return <Navigate to="/panel" replace />;

    return (
        <div className="pn">
            <p className="pn-sub">EN CONSTRUCCIÓN</p>
            <h1><PanelIcon name={item.icon} className="pn-titulo-icono" /> {item.label}</h1>
            <p className="pn-texto">{item.descripcion}</p>
        </div>
    );
}

export default SeccionPendiente;
