import { useEffect, useRef, useState } from "react";
import { Link, NavLink } from "react-router-dom";
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
  const wrapperRef = useRef(null);

  const closeMenu = () => setMenuOpen(false);

  // Cierra el menú al hacer clic fuera o al presionar Escape
  useEffect(() => {
    if (!menuOpen) return;

    const handleClickOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };
    const handleEscape = (e) => {
      if (e.key === "Escape") setMenuOpen(false);
    };

    document.addEventListener("mousedown", handleClickOutside);
    document.addEventListener("keydown", handleEscape);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [menuOpen]);

  return (
    <header className="navbar">
      <Link to="/" className="navbar-logo" onClick={closeMenu}>
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
        <Link to="/login" className="nav-login">
          Ingresar
        </Link>
        <Link to="/register" className="nav-register">
          Inscríbete
        </Link>

        <div className="menu-wrapper" ref={wrapperRef}>
          <button
            className="menu-btn"
            onClick={() => setMenuOpen(!menuOpen)}
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
                onClick={closeMenu}
              >
                {label}
              </NavLink>
            ))}
          </nav>
        </div>
      </div>
    </header>
  );
}

export default Navbar;