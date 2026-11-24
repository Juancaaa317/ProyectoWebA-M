import { Link } from "react-router-dom";
import { Sparkles, Activity, Flame, UserRound, ArrowRight } from "lucide-react";
import "../styles/clases.css";

// EJEMPLO: reemplazar por las clases reales de la academia
const clases = [
    {
        nombre: "Ballet",
        icono: Sparkles,
        etiqueta: "Técnica clásica",
        descripcion:
            "Técnica clásica, postura y elegancia. Para niñas, jóvenes y adultas de todos los niveles.",
    },
    {
        nombre: "Danza acrobática",
        icono: Activity,
        etiqueta: "Fuerza y control",
        descripcion:
            "Flexibilidad, fuerza y acrobacia combinadas con danza. Ideal para ganar confianza y control corporal.",
    },
    {
        nombre: "Danza árabe",
        icono: Flame,
        etiqueta: "Ritmo y expresión",
        descripcion:
            "Ritmo, expresión y movimiento fluido en un ambiente alegre y de mucha energía.",
    },
    {
        nombre: "Clase personalizada",
        icono: UserRound,
        etiqueta: "A tu medida",
        descripcion:
            "Sesiones a tu medida, con horario y objetivos acordados directamente con el profesor.",
    },
];

// Tiempos de conteo que flotan como apuntes de clase ("5, 6, 7, 8…")
const conteos = Array.from({ length: 12 }, (_, i) => ({
    valor: (i % 8) + 1,
    left: `${(i * 29 + 6) % 96}%`,
    size: 1.1 + ((i * 5) % 5) * 0.35,
    delay: `${-((i * 3.7) % 24)}s`,
    duration: `${20 + ((i * 3) % 14)}s`,
}));

// Sigue el cursor para iluminar la tarjeta
const seguirCursor = (e) => {
    const r = e.currentTarget.getBoundingClientRect();
    e.currentTarget.style.setProperty("--mx", `${e.clientX - r.left}px`);
    e.currentTarget.style.setProperty("--my", `${e.clientY - r.top}px`);
};

function Classes() {
    return (
        <section className="clases">
            {/* ===== Fondo animado: sala de ensayo ===== */}
            <div className="clases-bg" aria-hidden="true">
                <span className="clases-light clases-light--a"></span>
                <span className="clases-light clases-light--b"></span>

                {/* Piso del salón en perspectiva */}
                <div className="clases-floor"></div>

                {/* Barra de ballet */}
                <div className="clases-barre">
                    <span></span>
                    <span></span>
                </div>

                {/* Reflejo de espejo que recorre la sala */}
                <span className="clases-mirror"></span>

                {conteos.map((c, i) => (
                    <span
                        key={i}
                        className="clases-count"
                        style={{
                            "--left": c.left,
                            "--size": `${c.size}rem`,
                            "--delay": c.delay,
                            "--duration": c.duration,
                        }}
                    >
                        {c.valor}
                    </span>
                ))}
            </div>

            {/* ===== Encabezado ===== */}
            <div className="clases-header">
                <p className="clases-subtitle">
                    <span className="clases-subtitle-dot" aria-hidden="true"></span>
                    ARTE &amp; MOVIMIENTO
                </p>
                <h1>
                    Nuestras <span className="clases-gradient">clases</span>
                </h1>
                <p className="clases-lead">
                    Encuentra la disciplina que mejor va contigo.
                </p>
                <span className="clases-line" aria-hidden="true"></span>
            </div>

            {/* ===== Tarjetas ===== */}
            <div className="clases-grid">
                {clases.map(({ nombre, descripcion, etiqueta, icono: Icono }, i) => (
                    <article
                        key={nombre}
                        className="clases-card"
                        style={{ "--i": i }}
                        onMouseMove={seguirCursor}
                    >
                        <span className="clases-card-num" aria-hidden="true">
                            {String(i + 1).padStart(2, "0")}
                        </span>

                        <div className="clases-card-icon" aria-hidden="true">
                            <Icono strokeWidth={1.6} />
                        </div>

                        <span className="clases-card-tag">{etiqueta}</span>
                        <h2>{nombre}</h2>
                        <p>{descripcion}</p>

                        <Link to="/horario" className="clases-card-link">
                            Ver horario
                            <ArrowRight size={16} aria-hidden="true" />
                        </Link>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default Classes;
