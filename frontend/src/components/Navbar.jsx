import { useEffect, useRef, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/navbar.css";

// "Inicio" ya no va aquí: tiene su propio botón en la barra
const menuLinks = [
  {
    to: "/historia",
    label: "Historia",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z" />
      </svg>
    ),
  },
  {
    to: "/m-v",
    label: "Misión y Visión",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="9" />
        <circle cx="12" cy="12" r="5" />
        <circle cx="12" cy="12" r="1.2" fill="currentColor" />
      </svg>
    ),
  },
  {
    to: "/clases",
    label: "Clases",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 3v3m0 12v3M5.6 5.6l2.1 2.1m8.6 8.6 2.1 2.1M3 12h3m12 0h3M5.6 18.4l2.1-2.1m8.6-8.6 2.1-2.1" />
        <circle cx="12" cy="12" r="3" />
      </svg>
    ),
  },
  {
    to: "/horario",
    label: "Horario",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="5" width="18" height="16" rx="3" />
        <path d="M8 3v4m8-4v4M3 10h18" />
        <path d="M9 15l2 2 4-4" />
      </svg>
    ),
  },
  {
    to: "/profesores",
    label: "Profesores",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="8" r="4" />
        <path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5" />
      </svg>
    ),
  },
];

function HomeIcon() {
  return (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M3 11.5 12 4l9 7.5" />
      <path d="M5 10v9a1 1 0 0 0 1 1h4v-5h4v5h4a1 1 0 0 0 1-1v-9" />
    </svg>
  );
}

function ProfileIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="8" r="4" />
      <path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5" />
    </svg>
  );
}

function PanelIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="8" height="10" rx="2" />
      <rect x="13" y="3" width="8" height="6" rx="2" />
      <rect x="13" y="11" width="8" height="10" rx="2" />
      <rect x="3" y="15" width="8" height="6" rx="2" />
    </svg>
  );
}

function LogoutIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <path d="m16 17 5-5-5-5M21 12H9" />
    </svg>
  );
}

function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
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

  // La barra se compacta y gana sombra al bajar
  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 10);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

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
    <header className={scrolled ? "navbar navbar--scrolled" : "navbar"}>
      <Link
        to="/"
        className="navbar-logo"
        onClick={closeAll}
        aria-label="Arte y Movimiento, ir al inicio"
      >
        {/* Recuadro mejorado del logo */}
        <span className="navbar-logo-badge" aria-hidden="true">
          <span className="navbar-logo-ring"></span>
          <img
            src="/images/Logo1.png"
            alt="Logo de Arte y Movimiento"
            className="navbar-logo-img"
          />
        </span>

        <span className="navbar-logo-copy">
          <span className="navbar-logo-text">
            ARTE <span>&</span> MOVIMIENTO
          </span>
        </span>
      </Link>

      <div className="navbar-actions">
        {/* Botón de Inicio siempre visible */}
        <NavLink
          to="/"
          end
          onClick={closeAll}
          className={({ isActive }) => (isActive ? "nav-home active" : "nav-home")}
          aria-label="Inicio"
        >
          <HomeIcon />
          <span className="nav-home-label">Inicio</span>
        </NavLink>

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

        {/* Menú de navegación */}
        <div className="menu-wrapper" ref={menuRef}>
          <button
            className="menu-btn"
            onClick={toggleMenu}
            aria-label="Abrir o cerrar menú"
            aria-expanded={menuOpen}
            aria-controls="menu-dropdown"
          >
            <span className="menu-btn-label">Menú</span>
            <span className="burger" aria-hidden="true">
              <i></i>
              <i></i>
              <i></i>
            </span>
          </button>

          <nav
            id="menu-dropdown"
            className={menuOpen ? "menu-dropdown open" : "menu-dropdown"}
          >
            {/* Resplandor decorativo superior */}
            <div className="menu-dropdown-glow" aria-hidden="true"></div>

            <span className="menu-dropdown-title">
              <span className="menu-dropdown-title-dot" aria-hidden="true"></span>
              Explorar
            </span>

            <div className="menu-dropdown-links">
              {menuLinks.map(({ to, label, icon }, i) => (
                <NavLink
                  key={to}
                  to={to}
                  className={({ isActive }) =>
                    isActive ? "menu-link active" : "menu-link"
                  }
                  onClick={closeAll}
                  style={{ "--stagger": `${i * 0.05 + 0.08}s` }}
                >
                  <span className="menu-link-icon" aria-hidden="true">
                    {icon}
                  </span>
                  <span className="menu-link-label">{label}</span>
                  <span className="menu-link-arrow" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="m9 6 6 6-6 6" />
                    </svg>
                  </span>
                </NavLink>
              ))}
            </div>

            {/* Pie decorativo del menú */}
            <div className="menu-dropdown-footer" aria-hidden="true">
              Arte &amp; Movimiento · Cali, Colombia
            </div>
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
              <div className="menu-dropdown-glow" aria-hidden="true"></div>

              <div className="user-info">
                <div className="user-info-avatar" aria-hidden="true">
                  {inicial}
                </div>
                <div className="user-info-copy">
                  <strong>{user.correo}</strong>
                  <span>{user.rol}</span>
                </div>
              </div>

              <div className="menu-dropdown-links">
                <Link
                  to="/panel/perfil"
                  className="menu-link"
                  onClick={closeAll}
                  style={{ "--stagger": "0.08s" }}
                >
                  <span className="menu-link-icon" aria-hidden="true">
                    <ProfileIcon />
                  </span>
                  <span className="menu-link-label">Mi perfil</span>
                </Link>

                <Link
                  to="/panel"
                  className="menu-link"
                  onClick={closeAll}
                  style={{ "--stagger": "0.13s" }}
                >
                  <span className="menu-link-icon" aria-hidden="true">
                    <PanelIcon />
                  </span>
                  <span className="menu-link-label">Mi panel</span>
                </Link>

                <button
                  type="button"
                  className="menu-link menu-link--danger"
                  onClick={handleLogout}
                  style={{ "--stagger": "0.18s" }}
                >
                  <span className="menu-link-icon" aria-hidden="true">
                    <LogoutIcon />
                  </span>
                  <span className="menu-link-label">Cerrar sesión</span>
                </button>
              </div>
            </nav>
          </div>
        )}
      </div>
    </header>
  );
}

export default Navbar;