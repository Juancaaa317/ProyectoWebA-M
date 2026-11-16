import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { MENUS, ROL_INFO, rutaDe } from "../config/roles";
import PanelIcon from "../components/PanelIcon";

function Resumen() {
    const { user } = useAuth();
    const info = ROL_INFO[user.rol];
    const accesos = (MENUS[user.rol] ?? []).filter((i) => i.path !== "");

    return (
        <div className="pn pn-resumen">
            <p className="pn-sub">{info?.titulo.toUpperCase() ?? "PANEL"}</p>
            <h1>Hola, <span>{user.nombre || user.correo}</span></h1>
            <p className="pn-texto">{info?.bienvenida}</p>

            <div className="pn-seccion-titulo">
                <div>
                    <h2>Tu espacio en la academia</h2>
                    <p>Accede a tus herramientas y consulta la información de tu cuenta.</p>
                </div>
            </div>

            <div className="pn-grid">
                {accesos.map((item) => (
                    <Link key={item.path} to={rutaDe(item)} className="pn-card">
                        <span className="pn-card-icono">
                            <PanelIcon name={item.icon} className="pn-icono" />
                        </span>
                        <span className="pn-card-contenido">
                            <strong>{item.label}</strong>
                            <span>{item.descripcion || "Consulta y administra tu información personal."}</span>
                        </span>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default Resumen;
