import "../styles/profesores.css";

// EJEMPLO: reemplazar por los profesores reales de la academia
const profesores = [
    { nombre: "Nombre del profesor 1", area: "Ballet" },
    { nombre: "Nombre del profesor 2", area: "Danza acrobática" },
    { nombre: "Nombre del profesor 3", area: "Danza árabe" },
];

function Teachers() {
    return (
        <section className="profesores">
            <div className="profesores-header">
                <p className="profesores-subtitle">ARTE & MOVIMIENTO</p>
                <h1>Nuestros profesores</h1>
                <p>Un equipo comprometido con tu crecimiento artístico.</p>
            </div>

            <div className="profesores-grid">
                {profesores.map(({ nombre, area }) => (
                    <article key={nombre} className="profesores-card">
                        <div className="profesores-avatar">{nombre.charAt(0)}</div>
                        <h2>{nombre}</h2>
                        <p>{area}</p>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default Teachers;