import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageLayout from "../components/PageLayout";
import "../styles/historia.css";

const FOUNDING_YEAR = 2014;

const GENRES = [
  "Danza clásica",
  "Acrobática",
  "Contemporánea",
  "Jazz",
  "Ritmos latinos",
  "Urbanos",
];

const ROMAN = ["XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI"];

// Marcas del reloj (60 minutos, resaltando cada 5)
const TICKS = Array.from({ length: 60 }, (_, i) => {
  const angle = (i * 6 * Math.PI) / 180;
  const inner = i % 5 === 0 ? 168 : 178;
  return {
    key: i,
    x1: 200 + Math.sin(angle) * inner,
    y1: 200 - Math.cos(angle) * inner,
    x2: 200 + Math.sin(angle) * 190,
    y2: 200 - Math.cos(angle) * 190,
    major: i % 5 === 0,
  };
});

// Polvo de escenario reducido (14 partículas)
const DUST = Array.from({ length: 14 }, (_, i) => ({
  left: (i * 47) % 100,
  size: 3 + ((i * 5) % 5),
  duration: 22 + ((i * 7) % 16),
  delay: -((i * 9) % 28),
  drift: (i % 2 ? 1 : -1) * (20 + ((i * 13) % 50)),
  alpha: 0.3 + ((i * 11) % 35) / 100,
}));

function Slipper() {
  return (
    <svg
      viewBox="0 0 120 80"
      fill="none"
      stroke="currentColor"
      strokeWidth="2.5"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M10 48 C24 30 44 26 62 34 C78 41 96 38 110 26 C114 42 104 58 82 62 C56 66 26 66 10 48 Z" />
      <path d="M62 34 C66 46 62 56 56 63" />
      <path d="M44 31 C30 14 16 8 6 12 M50 30 C44 12 50 4 64 2" />
    </svg>
  );
}

function Historia() {
  const years = new Date().getFullYear() - FOUNDING_YEAR;

  const handleTilt = (e) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const px = (e.clientX - rect.left) / rect.width - 0.5;
    const py = (e.clientY - rect.top) / rect.height - 0.5;
    e.currentTarget.style.setProperty("--rx", `${(-py * 8).toFixed(2)}deg`);
    e.currentTarget.style.setProperty("--ry", `${(px * 10).toFixed(2)}deg`);
  };

  const resetTilt = (e) => {
    e.currentTarget.style.removeProperty("--rx");
    e.currentTarget.style.removeProperty("--ry");
  };

  return (
    <>
      <Navbar />
      <PageLayout>
        <main className="historia-page">
          {/* ===== Fondo: tiempo + danza, más limpio ===== */}
          <div className="hist-bg" aria-hidden="true">
            <span className="hist-orb hist-orb--purple"></span>
            <span className="hist-orb hist-orb--pink"></span>

            {/* Reloj antiguo girando lentamente */}
            <svg className="hist-clock" viewBox="0 0 400 400" fill="none" stroke="currentColor">
              <circle cx="200" cy="200" r="196" strokeWidth="1.5" />
              <circle cx="200" cy="200" r="120" strokeWidth="1" strokeDasharray="2 7" />
              <circle cx="200" cy="200" r="100" strokeWidth="1" />
              {TICKS.map((t) => (
                <line
                  key={t.key}
                  x1={t.x1}
                  y1={t.y1}
                  x2={t.x2}
                  y2={t.y2}
                  strokeWidth={t.major ? 2.5 : 1}
                  strokeLinecap="round"
                />
              ))}
              {ROMAN.map((n, i) => (
                <text
                  key={n}
                  x="200"
                  y="52"
                  transform={`rotate(${i * 30} 200 200)`}
                  textAnchor="middle"
                  fill="currentColor"
                  stroke="none"
                  fontSize="20"
                  fontFamily="Georgia, 'Times New Roman', serif"
                >
                  {n}
                </text>
              ))}
            </svg>

            {/* Cintas de satén: solo dos líneas */}
            <svg
              className="hist-ribbon"
              viewBox="0 0 1440 900"
              preserveAspectRatio="xMidYMid slice"
              fill="none"
            >
              <defs>
                <linearGradient id="hist-grad" x1="0" y1="0" x2="1" y2="0">
                  <stop offset="0%" stopColor="#8a2be2" />
                  <stop offset="100%" stopColor="#ff1f9c" />
                </linearGradient>
              </defs>
              <path
                className="hist-ribbon-line hist-ribbon-line--1"
                pathLength="1"
                d="M-60 760 C180 560 360 900 640 700 S1040 300 1280 430 S1520 640 1520 560"
              />
              <path
                className="hist-ribbon-line hist-ribbon-line--2"
                pathLength="1"
                d="M-60 800 C200 610 380 940 660 745 S1060 350 1300 480 S1540 680 1540 610"
              />
            </svg>

            {/* Dos zapatillas flotando */}
            <span className="hist-slipper hist-slipper--a"><Slipper /></span>
            <span className="hist-slipper hist-slipper--b"><Slipper /></span>

            {/* Polvo de escenario */}
            {DUST.map((d, i) => (
              <span
                key={i}
                className={`hist-dust ${i % 3 === 0 ? "hist-dust--pink" : "hist-dust--purple"}`}
                style={{
                  "--left": `${d.left}%`,
                  "--size": `${d.size}px`,
                  "--duration": `${d.duration}s`,
                  "--delay": `${d.delay}s`,
                  "--drift": `${d.drift}px`,
                  "--alpha": d.alpha,
                }}
              ></span>
            ))}

            <div className="hist-grain"></div>
          </div>

          {/* ===== Encabezado ===== */}
          <section className="historia-header">
            <span className="historia-year" aria-hidden="true">
              {FOUNDING_YEAR}
            </span>

            <span className="historia-subtitle">Nuestros Orígenes</span>

            <h1>
              Nuestra <span>Historia</span>
            </h1>

            <svg
              className="historia-swoosh"
              viewBox="0 0 360 24"
              fill="none"
              aria-hidden="true"
            >
              <defs>
                <linearGradient id="hist-grad-2" x1="0" y1="0" x2="1" y2="0">
                  <stop offset="0%" stopColor="#8a2be2" />
                  <stop offset="100%" stopColor="#ff1f9c" />
                </linearGradient>
              </defs>
              <path
                pathLength="1"
                d="M4 14 C60 2 110 22 170 11 S280 4 356 13"
                stroke="url(#hist-grad-2)"
                strokeWidth="4"
                strokeLinecap="round"
              />
            </svg>
          </section>

          {/* ===== Contenido ===== */}
          <section className="historia-content">
            <div className="historia-texto">
              <p className="historia-lead">
                Fundada en <strong>septiembre de 2014</strong>, nuestra academia
                nació para enseñar la <strong>técnica clásica</strong> en la{" "}
                <strong>primera infancia</strong>, respetando el desarrollo de
                los niños. Con el tiempo, incluimos géneros como Danza
                Acrobática, Contemporánea, Jazz, Ritmos Latinos y Urbanos,
                promoviendo la disciplina de la danza en todas las edades.
              </p>

              <p>
                Promovemos la danza como una herramienta fundamental para
                fortalecer el{" "}
                <strong>desarrollo motor, corporal, emocional, creativo y social</strong>,
                mediante experiencias de aprendizaje que favorecen la
                coordinación, el equilibrio, la expresión corporal, la
                disciplina, la autonomía y la confianza en sí mismos.
              </p>

              <ul className="historia-genres">
                {GENRES.map((genre) => (
                  <li key={genre}>{genre}</li>
                ))}
              </ul>
            </div>

            {/* Imagen con vida */}
            <div
              className="historia-imagen"
              onMouseMove={handleTilt}
              onMouseLeave={resetTilt}
            >
              <span className="hist-frame-back" aria-hidden="true"></span>
              <span className="hist-corner hist-corner--tl" aria-hidden="true"></span>
              <span className="hist-corner hist-corner--br" aria-hidden="true"></span>

              <div className="hist-frame">
                <img
                  src="/images/ImgPresentacion1.jpg"
                  alt="Historia de Arte y Movimiento"
                />
                <span className="hist-shine" aria-hidden="true"></span>
                <span className="hist-frame-sweep" aria-hidden="true"></span>
              </div>

              {/* Sello con texto circular */}
              <div className="hist-seal" aria-hidden="true">
                <svg viewBox="0 0 120 120">
                  <defs>
                    <path
                      id="hist-seal-path"
                      d="M60 60 m-46 0 a46 46 0 1 1 92 0 a46 46 0 1 1 -92 0"
                    />
                  </defs>
                  <text>
                    <textPath
                      href="#hist-seal-path"
                      textLength="284"
                      lengthAdjust="spacing"
                    >
                      ARTE Y MOVIMIENTO ✦ DESDE {FOUNDING_YEAR} ✦
                    </textPath>
                  </text>
                </svg>
                <span className="hist-seal-core">{FOUNDING_YEAR}</span>
              </div>

              <div className="hist-years">
                <strong>{years} años</strong>
                <span>enseñando danza</span>
              </div>
            </div>
          </section>
        </main>
      </PageLayout>
      <Footer />
    </>
  );
}

export default Historia;