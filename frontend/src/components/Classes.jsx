import { Link } from "react-router-dom";
import { Sparkles, Activity, Flame, UserRound, Wind, Music, ArrowRight, RotateCw } from "lucide-react";
import useCarga from "../hooks/useCarga";
import { ofertaService, CLASE_ABIERTA } from "../services/ofertaService";
import "../styles/clases.css";

// Icono según el nombre del programa; los programas nuevos usan uno genérico
const ICONOS = [
    [/ballet/i, Sparkles],
    [/acrob/i, Activity],
    [/[aá]rabe/i, Flame],
    [/contempor/i, Wind],
    [/personaliz/i, UserRound],
];
const iconoDe = (nombre) => ICONOS.find(([patron]) => patron.test(nombre))?.[1] ?? Music;

// "Ana", "Ana y Luis", "Ana, Luis y Marta"
const LISTA = new Intl.ListFormat("es", { type: "conjunction" });

// Programas activos + clases (ambas consultas son públicas)
const cargarOferta = () => Promise.all([ofertaService.programasActivos(), ofertaService.clases()]);

// Une cada programa con sus grupos abiertos y los profesores que los dictan
function armarTarjetas([programas, clases]) {
    return programas
        .slice()
        .sort((a, b) => a.nombrePrograma.localeCompare(b.nombrePrograma, "es"))
        .map((programa) => {
            const grupos = clases.filter((c) => c.idPrograma === programa.idPrograma && CLASE_ABIERTA.has(c.estadoClase));
            const profesores = [...new Set(grupos.map((c) => c.nombreProfesor).filter(Boolean))];
            const etiqueta = grupos.length === 0
                ? "Próximamente"
                : `${grupos.length} ${grupos.length === 1 ? "grupo abierto" : "grupos abiertos"}`;
            return { ...programa, grupos, profesores, etiqueta, Icono: iconoDe(programa.nombrePrograma) };
        });
}

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
    const { datos, cargando, error, reintentar } = useCarga(cargarOferta);
    const tarjetas = datos ? armarTarjetas(datos) : null;

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

            {/* ===== Tarjetas (datos reales de la API) ===== */}
            {cargando && (
                <div className="clases-grid" aria-busy="true" aria-label="Cargando clases">
                    {[0, 1, 2].map((i) => <div key={i} className="clases-card clases-card--cargando" style={{ "--i": i }} />)}
                </div>
            )}

            {error && (
                <div className="clases-estado" role="alert">
                    <p>No pudimos cargar las clases. {error}</p>
                    <button type="button" className="clases-reintentar" onClick={reintentar}>
                        <RotateCw size={16} aria-hidden="true" /> Reintentar
                    </button>
                </div>
            )}

            {tarjetas && tarjetas.length === 0 && (
                <p className="clases-estado" role="status">
                    Pronto publicaremos nuestra oferta de clases. ¡Vuelve muy pronto!
                </p>
            )}

            {tarjetas && tarjetas.length > 0 && (
                <div className="clases-grid">
                    {tarjetas.map(({ idPrograma, nombrePrograma, descripcion, etiqueta, profesores, Icono }, i) => (
                        <article
                            key={idPrograma}
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
                            <h2>{nombrePrograma}</h2>
                            <p>{descripcion || "Pregúntanos por los detalles de esta disciplina."}</p>

                            {profesores.length > 0 && (
                                <p className="clases-card-profes">
                                    Con {LISTA.format(profesores)}
                                </p>
                            )}

                            <Link to="/horario" className="clases-card-link">
                                Ver horario
                                <ArrowRight size={16} aria-hidden="true" />
                            </Link>
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
}

export default Classes;
