import { useState } from "react";
import "../styles/horario.css";

// EJEMPLO: reemplazar por el horario real de la academia
const horario = [
    {
        dia: "Lunes",
        clases: [
            { hora: "4:00 pm", nombre: "Ballet infantil" },
            { hora: "5:30 pm", nombre: "Danza acrobática" },
        ],
    },
    {
        dia: "Martes",
        clases: [
            { hora: "4:00 pm", nombre: "Danza árabe" },
            { hora: "6:00 pm", nombre: "Ballet juvenil" },
        ],
    },
    {
        dia: "Miércoles",
        clases: [
            { hora: "4:00 pm", nombre: "Ballet infantil" },
            { hora: "5:30 pm", nombre: "Danza acrobática" },
        ],
    },
    {
        dia: "Jueves",
        clases: [{ hora: "5:00 pm", nombre: "Clase personalizada" }],
    },
    {
        dia: "Viernes",
        clases: [{ hora: "4:00 pm", nombre: "Danza árabe" }],
    },
    {
        dia: "Sábado",
        clases: [
            { hora: "9:00 am", nombre: "Ballet infantil" },
            { hora: "11:00 am", nombre: "Danza acrobática" },
        ],
    },
];

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

            {/* ===== Paneles que se expanden ===== */}
            <div className="horario-panels">
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
                                        {clases.map(({ hora, nombre }) => (
                                            <li key={`${dia}-${hora}`}>
                                                <span className="horario-hora">{hora}</span>
                                                <span className="horario-clase">{nombre}</span>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </article>
                    );
                })}
            </div>
        </section>
    );
}

export default Horario;
