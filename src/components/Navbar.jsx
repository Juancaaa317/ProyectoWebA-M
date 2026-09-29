
import { useState } from "react";
import "../styles/navbar.css";

function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);

  const closeMenu = () => setMenuOpen(false);

  return (
    <header className="navbar">
      <a href="#inicio" className="navbar-logo">
        ARTE <span>&</span> MOVIMIENTO
      </a>

      <button
        className="menu-toggle"
        onClick={() => setMenuOpen(!menuOpen)}
        aria-label="Abrir o cerrar menú"
        aria-expanded={menuOpen}
      >
        {menuOpen ? "✕" : "☰"}
      </button>

      <nav className={menuOpen ? "nav-links active" : "nav-links"}>
        <a href="#inicio" onClick={closeMenu}>Inicio</a>
        <a href="#nosotros" onClick={closeMenu}>Nosotros</a>
        <a href="#profesores" onClick={closeMenu}>Profesores</a>
        <a href="#clases" onClick={closeMenu}>Clases</a>
        <a href="#logros" onClick={closeMenu}>Logros</a>
        <a href="#contacto" onClick={closeMenu}>Contacto</a>

        <a href="#inscripcion" className="nav-register" onClick={closeMenu}>
          Inscríbete
        </a>
      </nav>
    </header>
  );
}

export default Navbar;