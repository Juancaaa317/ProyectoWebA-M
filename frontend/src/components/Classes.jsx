import "../styles/clases.css";

// EJEMPLO: reemplazar por las clases reales de la academia
const clases = [
    {
        nombre: "Ballet",
        descripcion:
            "Técnica clásica, postura y elegancia. Para niñas, jóvenes y adultas de todos los niveles.",
    },
    {
        nombre: "Danza acrobática",
        descripcion:
            "Flexibilidad, fuerza y acrobacia combinadas con danza. Ideal para ganar confianza y control corporal.",
    },
    {
        nombre: "Danza árabe",
        descripcion:
            "Ritmo, expresión y movimiento fluido en un ambiente alegre y de mucha energía.",
    },
    {
        nombre: "Clase personalizada",
        descripcion:
            "Sesiones a tu medida, con horario y objetivos acordados directamente con el profesor.",
    },
];

function Classes() {
    return (
        <section className="clases">
            <div className="clases-header">
                <p className="clases-subtitle">ARTE & MOVIMIENTO</p>
                <h1>Nuestras clases</h1>
                <p>Encuentra la disciplina que mejor va contigo.</p>
            </div>

            <div className="clases-grid">
                {clases.map(({ nombre, descripcion }) => (
                    <article key={nombre} className="clases-card">
                        <h2>{nombre}</h2>
                        <p>{descripcion}</p>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default Classes;