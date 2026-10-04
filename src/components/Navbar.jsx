import { useState } from "react";
import { Link } from "react-router-dom"; // 1. Importamos Link
import "../styles/navbar.css";

function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);

  const closeMenu = () => setMenuOpen(false);

  return (
    <header className="navbar">
      {/* 2. El logo ahora lleva al inicio (/) */}
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

      <button
        className="menu-toggle"
        onClick={() => setMenuOpen(!menuOpen)}
        aria-label="Abrir o cerrar menú"
        aria-expanded={menuOpen}
      >
        {menuOpen ? "✕" : "☰"}
      </button>

      <nav className={menuOpen ? "nav-links active" : "nav-links"}>
        {/* 3. Inicio e Historia usan <Link> para navegar entre páginas */}
        <Link to="/" onClick={closeMenu}>
          Inicio
        </Link>
        <Link to="/historia" onClick={closeMenu}>
          Historia
        </Link>

        {/* Los demás mantienen <a> si son secciones dentro de la misma página */}
        <a href="#profesores" onClick={closeMenu}>
          Profesores
        </a>
        <a href="#clases" onClick={closeMenu}>
          Clases
        </a>
        <a href="#logros" onClick={closeMenu}>
          Logros
        </a>
        <a href="#contacto" onClick={closeMenu}>
          Contacto
        </a>

        <Link to="/login" className="nav-login" onClick={closeMenu}>
          Ingresar
        </Link>

        <Link to="/register" className="nav-register" onClick={closeMenu}>
          Inscríbete
        </Link>
        
      </nav>
    </header>
  );
}

export default Navbar;
