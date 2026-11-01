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

function Horario() {
    return (
        <section className="horario">
            <div className="horario-header">
                <p className="horario-subtitle">ARTE & MOVIMIENTO</p>
                <h1>Nuestro horario</h1>
                <p>Encuentra el día y la hora que mejor se adapten a ti.</p>
            </div>

            <div className="horario-grid">
                {horario.map(({ dia, clases }) => (
                    <article key={dia} className="horario-card">
                        <h2>{dia}</h2>
                        <ul>
                            {clases.map(({ hora, nombre }) => (
                                <li key={`${dia}-${hora}`}>
                                    <span className="horario-hora">{hora}</span>
                                    <span>{nombre}</span>
                                </li>
                            ))}
                        </ul>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default Horario;