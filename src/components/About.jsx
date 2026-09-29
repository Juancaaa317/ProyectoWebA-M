import { useEffect, useRef, useState } from "react";
import "../styles/about.css";

const stats = [
  { valor: 12, sufijo: "", texto: "Años de trayectoria" },
  { valor: 23, sufijo: "", texto: "Alumnos activos" },
  { valor: 6, sufijo: "", texto: "Estilos de baile" },
  { valor: 12, sufijo: "", texto: "Presentaciones" },
];

function Contador({ valor, sufijo }) {
  const [numero, setNumero] = useState(0);
  const ref = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (!entry.isIntersecting) return;
        observer.disconnect();

        const duracion = 1500;
        const inicio = performance.now();

        const animar = (ahora) => {
          const progreso = Math.min((ahora - inicio) / duracion, 1);
          setNumero(Math.floor(progreso * valor));
          if (progreso < 1) requestAnimationFrame(animar);
        };

        requestAnimationFrame(animar);
      },
      { threshold: 0.5 }
    );

    observer.observe(ref.current);
    return () => observer.disconnect();
  }, [valor]);

  return (
    <strong ref={ref}>
      {numero}
      {sufijo}
    </strong>
  );
}

function Nosotros() {
  return (
    <section id="nosotros" className="nosotros">
      <div className="nosotros-imagen">
        <img src="/images/nosotros.jpg" alt="Estudiantes de Arte y Movimiento bailando" />
      </div>

      <div className="nosotros-texto">
        <span className="section-label">NUESTRA HISTORIA</span>

        <h2>
          Una academia que <span>vuelve a moverse</span>
        </h2>

        <p>
          Arte y Movimiento nació del amor por el baile en Cali. Después de
          atravesar momentos difíciles y cambiar de ubicación, seguimos de pie
          gracias a nuestra comunidad y a nuestro compromiso con formar
          bailarines con disciplina y pasión.
        </p>

        <p>
          Hoy abrimos las puertas a nuevos estudiantes que quieran aprender,
          crecer y expresarse a través del movimiento.
        </p>

        <div className="nosotros-stats">
          {stats.map((s) => (
            <div className="stat" key={s.texto}>
              <Contador valor={s.valor} sufijo={s.sufijo} />
              <span>{s.texto}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default Nosotros;