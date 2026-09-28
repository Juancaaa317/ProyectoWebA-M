import './App.css'

function App() {
  return (
    <div className="app-container">
      {/* Barra de Navegación */}
      <nav className="navbar">
        <div className="logo">
          <h2>💃 Arte y Movimiento</h2>
        </div>
        <ul className="nav-links">
          <li><a href="#inicio">Inicio</a></li>
          <li><a href="#nosotros">Nuestra Historia</a></li>
          <li><a href="#clases">Clases</a></li>
          <li><button className="btn-login">Iniciar Sesión</button></li>
        </ul>
      </nav>

      {/* Sección Hero (Encabezado principal) */}
      <header id="inicio" className="hero-section">
        <div className="hero-content">
          <h1>Siente el ritmo, vive el movimiento</h1>
          <p>Tu academia de baile en Cali. Aprende Salsa Caleña, Bachata, Danza Urbana y más.</p>
          <button className="btn-primary">Inscríbete Ahora</button>
        </div>
      </header>

      {/* Contenido de la página */}
      <main>
        {/* Sección de Historia */}
        <section id="nosotros" className="about-section">
          <h2>Nuestra Historia</h2>
          <p>
            Superando los retos de la ciudad desde el 2021, Arte y Movimiento se ha renovado 
            para ofrecerte el mejor espacio de formación integral. Únete a nuestros más de 26 
            estudiantes activos y descubre tu pasión por el baile.
          </p>
        </section>

        {/* Sección de Clases */}
        <section id="clases" className="classes-section">
          <h2>Nuestros Ritmos</h2>
          <div className="classes-grid">
            <div className="class-card">
              <h3>Salsa Caleña</h3>
              <p>Nivel básico, intermedio y avanzado.</p>
            </div>
            <div className="class-card">
              <h3>Danza Urbana</h3>
              <p>Expresión corporal y ritmos modernos.</p>
            </div>
            <div className="class-card">
              <h3>Bachata</h3>
              <p>Pasos libres y baile en pareja.</p>
            </div>
          </div>
        </section>
      </main>

      {/* Pie de página */}
      <footer className="footer">
        <p>&copy; 2026 Academia Arte y Movimiento - Cali, Colombia. Proyecto Web UAO.</p>
      </footer>
    </div>
  )
}

export default App