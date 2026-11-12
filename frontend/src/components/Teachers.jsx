import { useRef } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import { EffectCreative, Autoplay } from "swiper/modules";
import "swiper/css";
import "swiper/css/effect-creative";
import "../styles/profesores.css";

// Carrusel creativo adaptado de Carousel_004 / Skiper UI (versión gratuita).

// EJEMPLO: reemplazar por los profesores reales de la academia
const profesores = [
  {
    nombre: "Nombre del profesor 1",
    area: "Ballet",
  },
  {
    nombre: "Nombre del profesor 2",
    area: "Danza acrobática",
  },
  {
    nombre: "Nombre del profesor 3",
    area: "Danza árabe",
  },
  {
    nombre: "Nombre del profesor 4",
    area: "Contemporánea",
  },
  {
    nombre: "Nombre del profesor 5",
    area: "Ritmos latinos",
  },
];

function Teachers() {
  const swiperRef = useRef(null);

  return (
    <section className="profesores">
      {/* ===== Fondo animado formal ===== */}
      <div className="prof-bg" aria-hidden="true">
        {/* Focos de escenario sutiles */}
        <span className="prof-beam prof-beam--left"></span>
        <span className="prof-beam prof-beam--right"></span>

        {/* Resplandores de color */}
        <span className="prof-orb prof-orb--purple"></span>
        <span className="prof-orb prof-orb--pink"></span>

        {/* Rejilla tenue */}
        <div className="prof-grid"></div>

        {/* Partículas de polvo de escenario */}
        {Array.from({ length: 10 }, (_, i) => (
          <span
            key={i}
            className="prof-dust"
            style={{
              "--left": `${(i * 41) % 100}%`,
              "--delay": `${-((i * 3) % 12)}s`,
              "--duration": `${11 + ((i * 4) % 8)}s`,
              "--drift": `${(i % 2 ? 1 : -1) * (20 + ((i * 7) % 40))}px`,
            }}
          ></span>
        ))}

        {/* Viñeta inferior para anclar la vista */}
        <div className="prof-vignette"></div>
      </div>

      {/* ===== Encabezado ===== */}
      <div className="profesores-header">
        <span className="profesores-subtitle">
          <span className="profesores-subtitle-dot" aria-hidden="true"></span>
          Arte &amp; Movimiento
        </span>
        <h1>
          Nuestros <span className="prof-gradient">Profesores</span>
        </h1>
        <p className="profesores-lead">
          Un equipo comprometido con tu crecimiento artístico.
        </p>
        <span className="profesores-line" aria-hidden="true"></span>
      </div>

      {/* ===== Carrusel ===== */}
      <div className="prof-carrusel">
        <Swiper
          modules={[EffectCreative, Autoplay]}
          effect="creative"
          grabCursor
          centeredSlides
          loop
          speed={750}
          slideToClickedSlide
          autoplay={{ delay: 5500, disableOnInteraction: false, pauseOnMouseEnter: true }}
          slidesPerView={1}
          creativeEffect={{
            limitProgress: 1,
            prev: {
              shadow: false,
              translate: ["-62%", 0, -160],
              rotate: [0, 18, 0],
              opacity: 0.5,
            },
            next: {
              shadow: false,
              translate: ["62%", 0, -160],
              rotate: [0, -18, 0],
              opacity: 0.5,
            },
          }}
          onSwiper={(swiper) => {
            swiperRef.current = swiper;
          }}
          className="prof-swiper"
        >
          {profesores.map((prof, i) => (
            <SwiperSlide key={prof.nombre}>
              <article className="profesores-card">
                {/* Número decorativo */}
                <span className="profesores-num" aria-hidden="true">
                  {String(i + 1).padStart(2, "0")}
                </span>

                {/* Avatar con anillo */}
                <div className="profesores-avatar-wrap" aria-hidden="true">
                  <span className="profesores-avatar-ring"></span>
                  <div className="profesores-avatar">
                    {prof.nombre.charAt(0)}
                  </div>
                </div>

                {/* Información simple */}
                <h2>{prof.nombre}</h2>
                <span className="profesores-area">{prof.area}</span>
              </article>
            </SwiperSlide>
          ))}
        </Swiper>

        {/* Navegación personalizada */}
        <button type="button" className="prof-nav prof-nav--prev" aria-label="Profesor anterior" onClick={() => swiperRef.current?.slidePrev()}>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="m15 6-6 6 6 6" />
          </svg>
        </button>
        <button type="button" className="prof-nav prof-nav--next" aria-label="Profesor siguiente" onClick={() => swiperRef.current?.slideNext()}>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="m9 6 6 6-6 6" />
          </svg>
        </button>
      </div>

      {/* Indicador de arrastre */}
      <p className="prof-hint" aria-hidden="true">
        Desliza para conocer al equipo
      </p>
    </section>
  );
}

export default Teachers;
