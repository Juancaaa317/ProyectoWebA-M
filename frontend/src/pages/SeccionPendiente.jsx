import { Navigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { MENUS } from "../config/roles";

function SeccionPendiente() {
    const { seccion } = useParams();
    const { user } = useAuth();

    const item = (MENUS[user.rol] ?? []).find((i) => i.path === seccion);

    // Si la sección no existe para este rol, vuelve al resumen
    if (!item) return <Navigate to="/panel" replace />;

    return (
        <div className="pn">
            <p className="pn-sub">EN CONSTRUCCIÓN</p>
            <h1>
                {item.icon} {item.label}
            </h1>
            <p className="pn-texto">{item.descripcion}</p>

            {item.datos && (
                <div className="pn-datos">
                    <span>Usará los datos de:</span>
                    {item.datos.map((d) => (
                        <code key={d}>{d}</code>
                    ))}
                </div>
            )}
        </div>
    );
}

export default SeccionPendiente;