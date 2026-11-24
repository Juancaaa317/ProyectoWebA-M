import { useRef } from "react";
import { RotateCw } from "lucide-react";
import { Swiper, SwiperSlide } from "swiper/react";
import { EffectCreative, Autoplay } from "swiper/modules";
import "swiper/css";
import "swiper/css/effect-creative";
import useCarga from "../hooks/useCarga";
import { ofertaService } from "../services/ofertaService";
import "../styles/profesores.css";

// Carrusel creativo adaptado de Carousel_004 / Skiper UI (versión gratuita).

const AREAS = { DOCENTE: "Docente", PROFESIONAL: "Profesional", ESPECIALIZADO: "Especializado" };

// Lo que muestra la etiqueta de la tarjeta: las disciplinas que dicta o, si aún no tiene clases, su área
const etiquetaDe = ({ disciplinas, areaDocencia }) =>
  disciplinas.length > 0 ? disciplinas.join(" · ") : AREAS[areaDocencia] ?? "Equipo docente";

const cargarProfesores = () => ofertaService.profesores();

function Teachers() {
  const swiperRef = useRef(null);
  const { datos: profesores, cargando, error, reintentar } = useCarga(cargarProfesores);
  // El loop de Swiper necesita al menos 3 tarjetas; con menos, las flechas vuelven al inicio (rewind)
  const enBucle = (profesores?.length ?? 0) >= 3;
  const variosProfesores = (profesores?.length ?? 0) > 1;

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

      {/* ===== Estados de carga, error y vacío ===== */}
      {cargando && (
        <p className="prof-estado" role="status" aria-busy="true">
          <span className="prof-estado-punto" aria-hidden="true"></span>
          Cargando el equipo…
        </p>
      )}

      {error && (
        <div className="prof-estado" role="alert">
          <p>No pudimos cargar a los profesores. {error}</p>
          <button type="button" className="prof-reintentar" onClick={reintentar}>
            <RotateCw size={16} aria-hidden="true" /> Reintentar
          </button>
        </div>
      )}

      {profesores && profesores.length === 0 && (
        <p className="prof-estado" role="status">
          Muy pronto presentaremos a nuestro equipo docente.
        </p>
      )}

      {/* ===== Carrusel (datos reales de la API) ===== */}
      {profesores && profesores.length > 0 && <>
      <div className="prof-carrusel">
        <Swiper
          modules={[EffectCreative, Autoplay]}
          effect="creative"
          grabCursor
          centeredSlides
          loop={enBucle}
          rewind={!enBucle}
          speed={750}
          slideToClickedSlide
          autoplay={variosProfesores && { delay: 5500, disableOnInteraction: false, pauseOnMouseEnter: true }}
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
            <SwiperSlide key={prof.idProfesor}>
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
                <span className="profesores-area">{etiquetaDe(prof)}</span>
              </article>
            </SwiperSlide>
          ))}
        </Swiper>

        {/* Navegación personalizada (solo si hay a dónde moverse) */}
        {variosProfesores && <>
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
        </>}
      </div>

      {/* Indicador de arrastre */}
      <p className="prof-hint" aria-hidden="true">
        Desliza para conocer al equipo
      </p>
      </>}
    </section>
  );
}

export default Teachers;
