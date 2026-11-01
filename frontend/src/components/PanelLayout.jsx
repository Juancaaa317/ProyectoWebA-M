import { useState } from "react";
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { MENUS, ROL_INFO, rutaDe } from "../config/roles";
import "../styles/adminLayout.css";

function PanelLayout() {
    const [abierto, setAbierto] = useState(false);
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const menu = MENUS[user.rol] ?? [];
    const titulo = ROL_INFO[user.rol]?.titulo ?? "Panel";

    const cerrarMenu = () => setAbierto(false);

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    return (
        <div className="al">
            {/* Barra superior: solo se ve en móvil */}
            <header className="al-topbar">
                <button
                    type="button"
                    className="al-topbar-btn"
                    onClick={() => setAbierto(true)}
                    aria-label="Abrir menú"
                >
                    ☰
                </button>
                <span className="al-topbar-titulo">{titulo}</span>
            </header>

            {abierto && <div className="al-overlay" onClick={cerrarMenu} />}

            <aside className={abierto ? "al-sidebar abierto" : "al-sidebar"}>
                <Link to="/" className="al-logo" onClick={cerrarMenu}>
                    <img src="/images/Logo1.png" alt="Arte y Movimiento" />
                    <span>
                        ARTE <b>&</b> MOVIMIENTO
                    </span>
                </Link>

                <div className="al-perfil">
                    <div className="al-avatar">
                        {user.correo?.charAt(0).toUpperCase() ?? "?"}
                    </div>
                    <strong>{user.correo}</strong>
                    <span className="al-rol">{user.rol}</span>
                </div>

                <nav className="al-menu">
                    {menu.map((item) => (
                        <NavLink
                            key={item.path || "resumen"}
                            to={rutaDe(item)}
                            end={item.path === ""}
                            className={({ isActive }) =>
                                isActive ? "al-link activo" : "al-link"
                            }
                            onClick={cerrarMenu}
                        >
                            <span className="al-icono">{item.icon}</span>
                            {item.label}
                            {item.pronto && <span className="al-pronto">Pronto</span>}
                        </NavLink>
                    ))}
                </nav>

                <div className="al-pie">
                    <Link to="/" className="al-link" onClick={cerrarMenu}>
                        <span className="al-icono">🌐</span>
                        Ver sitio
                    </Link>
                    <button type="button" className="al-link" onClick={handleLogout}>
                        <span className="al-icono">🚪</span>
                        Cerrar sesión
                    </button>
                </div>
            </aside>

            <div className="al-contenido">
                <Outlet />
            </div>
        </div>
    );
}

export default PanelLayout;