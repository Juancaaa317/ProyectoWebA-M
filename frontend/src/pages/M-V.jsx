import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageLayout from "../components/PageLayout";
import "../styles/M-V.css";

// Siluetas de bailarines
const POSES = [
  // Salto con brazos arriba
  <g key="jump">
    <circle cx="50" cy="16" r="6.5" fill="currentColor" stroke="none" />
    <path d="M50 28 L50 55 M50 34 L30 16 M50 34 L70 12 M50 55 L28 72 L36 92 M50 55 L72 70 L66 92" />
  </g>,
  // Arabesque
  <g key="arabesque">
    <circle cx="40" cy="18" r="6.5" fill="currentColor" stroke="none" />
    <path d="M42 28 L48 56 M42 34 L16 30 M42 34 L68 24 M48 56 L46 96 M48 56 L78 58 L94 48" />
  </g>,
  // Giro con vestido
  <g key="twirl">
    <circle cx="50" cy="16" r="6.5" fill="currentColor" stroke="none" />
    <path d="M50 30 Q28 28 26 10 M50 30 Q72 28 74 10 M42 76 L40 96 M58 76 L60 96" />
    <path d="M50 30 L28 74 Q50 84 72 74 Z" fill="currentColor" />
  </g>,
  // Pose urbana
  <g key="urban">
    <circle cx="46" cy="18" r="6.5" fill="currentColor" stroke="none" />
    <path d="M48 28 L52 56 M48 34 L74 8 M48 34 L28 48 L16 38 M52 56 L72 72 L66 94 M52 56 L34 74 L24 92" />
  </g>,
];

// Valores deterministas para render estable
const DANCERS = Array.from({ length: 12 }, (_, i) => ({
  pose: i % POSES.length,
  left: (i * 67) % 100,
  size: 42 + ((i * 17) % 42),
  duration: 20 + ((i * 7) % 14),
  delay: -((i * 6) % 26),
  drift: (i % 2 ? 1 : -1) * (30 + ((i * 11) % 60)),
  alpha: 0.18 + ((i * 13) % 22) / 100,
  blur: i % 4 === 0 ? 2 : 0,
  dance: 1.2 + ((i * 3) % 10) / 10,
}));

// Partículas que ascienden
const PARTICLES = Array.from({ length: 18 }, (_, i) => ({
  left: (i * 53) % 100,
  size: 3 + ((i * 7) % 5),
  duration: 9 + ((i * 5) % 10),
  delay: -((i * 4) % 14),
  drift: (i % 2 ? 1 : -1) * (20 + ((i * 9) % 60)),
}));

const CARDS = [
  {
    id: "mision",
    title: "Misión",
    watermark: "a&m",
    tags: ["Danza", "Arte", "Desarrollo integral"],
    text: "Arte y Movimiento (a&m) es una academia ubicada en la ciudad Cali (Colombia), que se enfoca en el desarrollo artístico e integral de sus estudiantes, a través de técnicas básicas de la danza y el arte, en donde se enfoca en el aprendizaje y desarrollo corporal humano.",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="9" />
        <circle cx="12" cy="12" r="5" />
        <circle cx="12" cy="12" r="1.2" fill="currentColor" />
      </svg>
    ),
  },
  {
    id: "vision",
    title: "Visión",
    watermark: "2028",
    tags: ["Reconocimiento nacional", "Formación integral"],
    text: "En el 2028 Arte y Movimiento (a&m) será reconocida a nivel nacional como una institución artística que fomenta y prepara a sus estudiantes integralmente, respetando sus procesos de formación, aprendizaje y desarrollo corporal.",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        <path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12Z" />
        <circle cx="12" cy="12" r="3" />
      </svg>
    ),
  },
];

function MV() {
  // Luz radial + tilt 3D al mover el cursor sobre la tarjeta
  const handleMove = (e) => {
    const card = e.currentTarget;
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    card.style.setProperty("--x", `${x}px`);
    card.style.setProperty("--y", `${y}px`);

    // Solo aplicar tilt en pantallas grandes
    if (window.innerWidth > 900) {
      const ry = ((x / rect.width) - 0.5) * 8;   // -4° a 4°
      const rx = ((0.5 - y / rect.height)) * 8;  // -4° a 4°
      card.style.setProperty("--rx", `${rx}deg`);
      card.style.setProperty("--ry", `${ry}deg`);
    }
  };

  // Restablecer el tilt al salir
  const handleLeave = (e) => {
    e.currentTarget.style.setProperty("--rx", "0deg");
    e.currentTarget.style.setProperty("--ry", "0deg");
  };

  return (
    <>
      <Navbar />

      <PageLayout>
        <main className="mv-page">
          {/* Fondo animado */}
          <div className="mv-bg" aria-hidden="true">
            <div className="mv-grid"></div>
            <span className="mv-orb mv-orb--purple"></span>
            <span className="mv-orb mv-orb--pink"></span>
            <span className="mv-orb mv-orb--violet"></span>

            {PARTICLES.map((p, i) => (
              <span
                key={`p-${i}`}
                className="mv-particle"
                style={{
                  "--px": `${p.left}%`,
                  "--ps": `${p.size}px`,
                  "--pd": `${p.duration}s`,
                  "--pdel": `${p.delay}s`,
                  "--pdrift": `${p.drift}px`,
                }}
              ></span>
            ))}

            {DANCERS.map((d, i) => (
              <span
                key={i}
                className={`mv-dancer ${i % 3 === 0 ? "mv-dancer--pink" : "mv-dancer--purple"}`}
                style={{
                  "--left": `${d.left}%`,
                  "--size": `${d.size}px`,
                  "--duration": `${d.duration}s`,
                  "--delay": `${d.delay}s`,
                  "--drift": `${d.drift}px`,
                  "--alpha": d.alpha,
                  "--blur": `${d.blur}px`,
                  "--dance": `${d.dance}s`,
                }}
              >
                <svg
                  viewBox="0 0 100 100"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="5.5"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  {POSES[d.pose]}
                </svg>
              </span>
            ))}

            <div className="mv-grain"></div>
          </div>

          {/* Encabezado */}
          <section className="mv-section">
            <span className="mv-subtitle">Nuestro Propósito</span>
            <h1>
              Misión y <span className="mv-gradient-text">Visión</span>
            </h1>
            <p className="mv-description">
              Descubre lo que nos mueve cada día: formar artistas completos,
              con cuerpo, mente y espíritu en constante evolución.
            </p>
          </section>

          {/* Tarjetas */}
          <section className="mv-content">
            {CARDS.map((card, index) => (
              <article
                key={card.id}
                className={`mv-card mv-card--${card.id}`}
                style={{ "--enter-delay": `${0.5 + index * 0.15}s` }}
                onMouseMove={handleMove}
                onMouseLeave={handleLeave}
              >
                <span className="mv-watermark" aria-hidden="true">
                  {card.watermark}
                </span>

                <div className="mv-icon" aria-hidden="true">
                  {card.icon}
                </div>

                <h2 className="mv-card-title">{card.title}</h2>
                <span className="mv-card-divider" aria-hidden="true"></span>

                <p className="mv-card-description">{card.text}</p>

                <ul className="mv-tags">
                  {card.tags.map((tag) => (
                    <li key={tag}>{tag}</li>
                  ))}
                </ul>
              </article>
            ))}
          </section>
        </main>
      </PageLayout>

      <Footer />
    </>
  );
}

export default MV;