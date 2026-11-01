import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { MENUS, ROL_INFO, rutaDe } from "../config/roles";

function Resumen() {
    const { user } = useAuth();
    const info = ROL_INFO[user.rol];
    const accesos = (MENUS[user.rol] ?? []).filter((i) => i.path !== "");

    return (
        <div className="pn">
            <p className="pn-sub">{info?.titulo.toUpperCase() ?? "PANEL"}</p>
            <h1>Hola, {user.correo}</h1>
            <p className="pn-texto">{info?.bienvenida}</p>

            <div className="pn-grid">
                {accesos.map((item) => (
                    <Link key={item.path} to={rutaDe(item)} className="pn-card">
                        <span className="pn-icono">{item.icon}</span>
                        <strong>{item.label}</strong>
                        {item.pronto && <span className="al-pronto">Pronto</span>}
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default Resumen;