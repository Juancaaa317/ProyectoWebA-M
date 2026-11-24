import { useState } from "react";
import { RotateCw } from "lucide-react";
import useCarga from "../hooks/useCarga";
import { ofertaService, CLASE_ABIERTA } from "../services/ofertaService";
import "../styles/horario.css";

const DIAS = [
    ["LUNES", "Lunes"], ["MARTES", "Martes"], ["MIERCOLES", "Miércoles"], ["JUEVES", "Jueves"],
    ["VIERNES", "Viernes"], ["SABADO", "Sábado"], ["DOMINGO", "Domingo"],
];

// "16:00:00" -> "4:00 pm"
const horaBonita = (hora) => {
    const [h, m] = String(hora).split(":").map(Number);
    const sufijo = h >= 12 ? "pm" : "am";
    return `${h % 12 || 12}:${String(m).padStart(2, "0")} ${sufijo}`;
};

// Horarios + clases (para saber si la clase está abierta y quién la dicta). Ambas consultas son públicas.
const cargarHorario = () => Promise.all([ofertaService.horarios(), ofertaService.clases()]);

// Agrupa por día (lunes a domingo) solo los horarios de clases abiertas, ordenados por hora
function armarDias([horarios, clases]) {
    const claseAbierta = new Map(clases.filter((c) => CLASE_ABIERTA.has(c.estadoClase)).map((c) => [c.idClase, c]));
    return DIAS
        .map(([clave, dia]) => ({
            dia,
            clases: horarios
                .filter((h) => h.diaSemana === clave && claseAbierta.has(h.idClase))
                .sort((a, b) => String(a.horaInicio).localeCompare(String(b.horaInicio)))
                .map((h) => ({
                    id: h.idHorario,
                    hora: horaBonita(h.horaInicio),
                    fin: horaBonita(h.horaFin),
                    nombre: h.nombreClase,
                    profesor: claseAbierta.get(h.idClase).nombreProfesor,
                })),
        }))
        .filter(({ clases: delDia }) => delDia.length > 0);
}

// Elementos decorativos del fondo (calendarios y números flotantes)
const calendarios = Array.from({ length: 7 }, (_, i) => ({
    numero: [3, 12, 21, 8, 17, 26, 30][i],
    left: `${(i * 15 + 4) % 96}%`,
    size: 46 + ((i * 11) % 26),
    delay: `${-(i * 4.3)}s`,
    duration: `${24 + ((i * 5) % 14)}s`,
    tilt: `${(i % 2 ? 1 : -1) * (6 + ((i * 5) % 14))}deg`,
}));

const numeros = Array.from({ length: 14 }, (_, i) => ({
    valor: String((i * 7 + 3) % 31 + 1).padStart(2, "0"),
    left: `${(i * 23 + 7) % 98}%`,
    size: 0.95 + ((i * 3) % 6) * 0.22,
    delay: `${-((i * 3.1) % 22)}s`,
    duration: `${18 + ((i * 4) % 16)}s`,
}));

function Horario() {
    const [activo, setActivo] = useState(0);
    const { datos, cargando, error, reintentar } = useCarga(cargarHorario);
    const horario = datos ? armarDias(datos) : null;

    return (
        <section className="horario">
            {/* ===== Fondo animado ===== */}
            <div className="horario-bg" aria-hidden="true">
                <span className="horario-orb horario-orb--purple"></span>
                <span className="horario-orb horario-orb--pink"></span>
                <div className="horario-bg-grid"></div>

                {numeros.map((n, i) => (
                    <span
                        key={`n${i}`}
                        className="horario-num"
                        style={{
                            "--left": n.left,
                            "--size": `${n.size}rem`,
                            "--delay": n.delay,
                            "--duration": n.duration,
                        }}
                    >
                        {n.valor}
                    </span>
                ))}

                {calendarios.map((c, i) => (
                    <div
                        key={`c${i}`}
                        className="horario-cal"
                        style={{
                            "--left": c.left,
                            "--size": `${c.size}px`,
                            "--delay": c.delay,
                            "--duration": c.duration,
                            "--tilt": c.tilt,
                        }}
                    >
                        <i></i>
                        <b>{c.numero}</b>
                    </div>
                ))}
            </div>

            {/* ===== Título ===== */}
            <div className="horario-header">
                <p className="horario-subtitle">
                    <span className="horario-subtitle-dot" aria-hidden="true"></span>
                    ARTE &amp; MOVIMIENTO
                </p>
                <h1>
                    Nuestro <span className="horario-gradient">horario</span>
                </h1>
                <p className="horario-lead">
                    Encuentra el día y la hora que mejor se adapten a ti.
                </p>
                <span className="horario-line" aria-hidden="true"></span>
            </div>

            {/* ===== Estados de carga, error y vacío ===== */}
            {cargando && (
                <p className="horario-estado" role="status" aria-busy="true">
                    <span className="horario-estado-punto" aria-hidden="true"></span>
                    Cargando el horario…
                </p>
            )}

            {error && (
                <div className="horario-estado" role="alert">
                    <p>No pudimos cargar el horario. {error}</p>
                    <button type="button" className="horario-reintentar" onClick={reintentar}>
                        <RotateCw size={16} aria-hidden="true" /> Reintentar
                    </button>
                </div>
            )}

            {horario && horario.length === 0 && (
                <p className="horario-estado" role="status">
                    Aún no hay horarios publicados. ¡Vuelve muy pronto!
                </p>
            )}

            {/* ===== Paneles que se expanden (datos reales de la API) ===== */}
            {horario && horario.length > 0 && <div className="horario-panels">
                {horario.map(({ dia, clases }, i) => {
                    const abierto = activo === i;
                    return (
                        <article
                            key={dia}
                            className={`horario-panel${abierto ? " is-active" : ""}`}
                            onMouseEnter={() => setActivo(i)}
                        >
                            <button
                                type="button"
                                className="horario-tab"
                                aria-expanded={abierto}
                                aria-controls={`horario-${i}`}
                                onClick={() => setActivo(i)}
                                onFocus={() => setActivo(i)}
                            >
                                <span className="horario-tab-num">
                                    {String(i + 1).padStart(2, "0")}
                                </span>
                                <span className="horario-tab-dia">{dia}</span>
                            </button>

                            <div
                                id={`horario-${i}`}
                                className="horario-body"
                                inert={!abierto}
                            >
                                <div className="horario-body-inner">
                                    <span className="horario-body-num" aria-hidden="true">
                                        {String(i + 1).padStart(2, "0")}
                                    </span>
                                    <h2>{dia}</h2>
                                    <p className="horario-count">
                                        {clases.length}{" "}
                                        {clases.length === 1 ? "clase" : "clases"}
                                    </p>
                                    <ul>
                                        {clases.map(({ id, hora, fin, nombre, profesor }) => (
                                            <li key={id}>
                                                <span className="horario-hora">{hora}</span>
                                                <span className="horario-clase">
                                                    {nombre}
                                                    <small className="horario-detalle">
                                                        Hasta las {fin}{profesor ? ` · ${profesor}` : ""}
                                                    </small>
                                                </span>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </article>
                    );
                })}
            </div>}
        </section>
    );
}

export default Horario;
