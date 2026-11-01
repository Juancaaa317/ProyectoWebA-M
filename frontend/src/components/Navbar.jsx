import { useEffect, useRef, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/navbar.css";

const menuLinks = [
  { to: "/", label: "Inicio" },
  { to: "/historia", label: "Historia" },
  { to: "/m-v", label: "Misión y Visión" },
  { to: "/clases", label: "Clases" },
  { to: "/horario", label: "Horario" },
  { to: "/profesores", label: "Profesores" },
];

function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const menuRef = useRef(null);
  const userRef = useRef(null);

  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const closeAll = () => {
    setMenuOpen(false);
    setUserMenuOpen(false);
  };

  const toggleMenu = () => {
    setUserMenuOpen(false);
    setMenuOpen((v) => !v);
  };

  const toggleUserMenu = () => {
    setMenuOpen(false);
    setUserMenuOpen((v) => !v);
  };

  const handleLogout = () => {
    closeAll();
    logout();
    navigate("/");
  };

  // Cierra los menús al hacer clic fuera o presionar Escape
  useEffect(() => {
    if (!menuOpen && !userMenuOpen) return;

    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
      if (userRef.current && !userRef.current.contains(e.target)) {
        setUserMenuOpen(false);
      }
    };
    const handleEscape = (e) => {
      if (e.key === "Escape") {
        setMenuOpen(false);
        setUserMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    document.addEventListener("keydown", handleEscape);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [menuOpen, userMenuOpen]);

  const inicial = user?.correo?.charAt(0).toUpperCase() ?? "?";

  return (
    <header className="navbar">
      <Link to="/" className="navbar-logo" onClick={closeAll}>
        <img
          src="/images/Logo1.png"
          alt="Logo de Arte y Movimiento"
          className="navbar-logo-img"
        />
        <span className="navbar-logo-text">
          ARTE <span>&</span> MOVIMIENTO
        </span>
      </Link>

      <div className="navbar-actions">
        {/* Solo se muestran si NO hay sesión */}
        {!user && (
          <>
            <Link to="/login" className="nav-login">
              Ingresar
            </Link>
            <Link to="/register" className="nav-register">
              Inscríbete
            </Link>
          </>
        )}

        {/* Menú de navegación (3 rayas) */}
        <div className="menu-wrapper" ref={menuRef}>
          <button
            className="menu-btn"
            onClick={toggleMenu}
            aria-label="Abrir o cerrar menú"
            aria-expanded={menuOpen}
            aria-controls="menu-dropdown"
          >
            {menuOpen ? "✕" : "☰"}
          </button>

          <nav
            id="menu-dropdown"
            className={menuOpen ? "menu-dropdown open" : "menu-dropdown"}
          >
            {menuLinks.map(({ to, label }) => (
              <NavLink
                key={to}
                to={to}
                end={to === "/"}
                className={({ isActive }) => (isActive ? "active" : "")}
                onClick={closeAll}
              >
                {label}
              </NavLink>
            ))}
          </nav>
        </div>

        {/* Menú de usuario: solo CON sesión */}
        {user && (
          <div className="menu-wrapper" ref={userRef}>
            <button
              className="user-btn"
              onClick={toggleUserMenu}
              aria-label="Menú de usuario"
              aria-expanded={userMenuOpen}
              aria-controls="user-dropdown"
            >
              {inicial}
            </button>

            <nav
              id="user-dropdown"
              className={
                userMenuOpen
                  ? "menu-dropdown user-dropdown open"
                  : "menu-dropdown user-dropdown"
              }
            >
              <div className="user-info">
                <strong>{user.correo}</strong>
                <span>{user.rol}</span>
              </div>

              {user.rol === "ADMIN" ? (
                <>
                  <Link to="/admin/perfil" onClick={closeAll}>
                    Mi perfil
                  </Link>
                  <Link to="/admin" onClick={closeAll}>
                    Panel de administración
                  </Link>
                </>
              ) : (
                <Link to="/perfil" onClick={closeAll}>
                  Mi perfil
                </Link>
              )}

              <button type="button" onClick={handleLogout}>
                Cerrar sesión
              </button>
            </nav>
          </div>
        )}
      </div>
    </header>
  );
}

export default Navbar;