import { useCallback, useEffect, useRef, useState } from "react";
import { createPortal } from "react-dom";
import { Link } from "react-router-dom";
import "../styles/heroCinematico.css";

// Portada "cinemática" de la página de inicio: escena con parallax, título editorial,
// presentación que se agranda con el scroll y los tres pilares de la academia.
// Para volver al Hero anterior basta con cambiar USAR_HERO_CINEMATICO en pages/Home.jsx.

// Fotos de la presentación (se ven en el visor a pantalla completa)
const GALERIA = [
    { src: "/images/ImgArco1.jpg", alt: "Bailarinas de Arte y Movimiento bajo un arco de escenario" },
    { src: "/images/ImgPresentacion1.jpg", alt: "Presentación de las alumnas de la academia" },
    { src: "/images/ImgAnita1.png", alt: "Bailarina de Arte y Movimiento en el escenario" },
];

// Si la academia tiene un video de presentación, poner aquí su ruta (ej. "/videos/presentacion.mp4")
// y el visor mostrará el video antes que las fotos. Mientras sea null, el visor es una galería.
const VIDEO_PRESENTACION = null;

const PILARES = [
    { palabra: "Técnica", detalle: "Bases sólidas y cuidado del cuerpo en cada clase." },
    { palabra: "Expresión", detalle: "El escenario como lugar para contar quién eres." },
    { palabra: "Comunidad", detalle: "Una familia que crece, ensaya y celebra unida." },
];

const limitar = (valor) => Math.min(1, Math.max(0, valor));

/**
 * Actualiza variables CSS según el scroll, sin volver a renderizar React (60 fps):
 * --hc-escena: 0 arriba del todo -> 1 cuando la escena ya salió de la vista
 * --hc-media:  0 cuando la presentación asoma por abajo -> 1 cuando llega al centro
 */
function useScrollCinematico(escenaRef, mediaRef) {
    useEffect(() => {
        const reducir = window.matchMedia("(prefers-reduced-motion: reduce)");
        let pendiente = 0;

        const actualizar = () => {
            pendiente = 0;
            const alto = window.innerHeight;
            const escena = escenaRef.current;
            const media = mediaRef.current;
            if (escena) {
                const r = escena.getBoundingClientRect();
                escena.style.setProperty("--hc-escena", limitar(-r.top / Math.max(r.height, 1)).toFixed(4));
            }
            if (media) {
                // Se mide la zona exterior (no escalada): medir el propio botón, que se escala, alteraría el cálculo
                const r = (media.parentElement ?? media).getBoundingClientRect();
                const centro = r.top + r.height / 2;
                // x1.15: el arco termina de abrirse un poco antes de llegar al centro exacto
                media.style.setProperty("--hc-media", limitar(1.15 * (alto - r.top) / (alto / 2 + r.height / 2)).toFixed(4));
                // Parallax interno: -1 (debajo del centro) a 1 (encima)
                media.style.setProperty("--hc-media-y", Math.max(-1, Math.min(1, (alto / 2 - centro) / alto)).toFixed(4));
            }
        };

        const programar = () => {
            if (!pendiente) pendiente = requestAnimationFrame(actualizar);
        };

        if (reducir.matches) {
            // Sin animación: todo en su estado final
            escenaRef.current?.style.setProperty("--hc-escena", "0");
            mediaRef.current?.style.setProperty("--hc-media", "1");
            mediaRef.current?.style.setProperty("--hc-media-y", "0");
            return undefined;
        }

        actualizar();
        window.addEventListener("scroll", programar, { passive: true });
        window.addEventListener("resize", programar);
        return () => {
            window.removeEventListener("scroll", programar);
            window.removeEventListener("resize", programar);
            if (pendiente) cancelAnimationFrame(pendiente);
        };
    }, [escenaRef, mediaRef]);
}

// Visor a pantalla completa. Va en un portal sobre <body>: la página de inicio está dentro de un
// contenedor con transform (transición de entrada) y eso desplazaría un elemento position: fixed.
function Visor({ inicial, onCerrar }) {
    const [indice, setIndice] = useState(inicial);
    const cerrarRef = useRef(null);
    const total = GALERIA.length + (VIDEO_PRESENTACION ? 1 : 0);
    const mover = useCallback((delta) => setIndice((i) => (i + delta + total) % total), [total]);

    useEffect(() => {
        const previo = document.body.style.overflow;
        document.body.style.overflow = "hidden";
        cerrarRef.current?.focus();
        const teclas = (e) => {
            if (e.key === "Escape") onCerrar();
            else if (e.key === "ArrowRight") mover(1);
            else if (e.key === "ArrowLeft") mover(-1);
        };
        document.addEventListener("keydown", teclas);
        return () => {
            document.body.style.overflow = previo;
            document.removeEventListener("keydown", teclas);
        };
    }, [onCerrar, mover]);

    const esVideo = VIDEO_PRESENTACION && indice === 0;
    const foto = GALERIA[indice - (VIDEO_PRESENTACION ? 1 : 0)];

    return createPortal(
        <div className="hc-visor" role="dialog" aria-modal="true" aria-label="Presentación de Arte y Movimiento" onClick={onCerrar}>
            <button ref={cerrarRef} type="button" className="hc-visor-cerrar" onClick={onCerrar}>
                <span aria-hidden="true">ESC</span> Cerrar
            </button>

            <figure className="hc-visor-figura" onClick={(e) => e.stopPropagation()}>
                {esVideo
                    ? <video src={VIDEO_PRESENTACION} controls autoPlay className="hc-visor-medio" />
                    : <img key={foto.src} src={foto.src} alt={foto.alt} className="hc-visor-medio" />}
                {!esVideo && <figcaption>{foto.alt}</figcaption>}
            </figure>

            {total > 1 && <>
                <button type="button" className="hc-visor-nav hc-visor-nav--prev" aria-label="Foto anterior"
                    onClick={(e) => { e.stopPropagation(); mover(-1); }}>‹</button>
                <button type="button" className="hc-visor-nav hc-visor-nav--next" aria-label="Foto siguiente"
                    onClick={(e) => { e.stopPropagation(); mover(1); }}>›</button>
                <p className="hc-visor-contador" aria-live="polite">{indice + 1} / {total}</p>
            </>}
        </div>,
        document.body,
    );
}

function HeroCinematico() {
    const escenaRef = useRef(null);
    const mediaRef = useRef(null);
    const abrirRef = useRef(null);
    const [visorAbierto, setVisorAbierto] = useState(false);
    useScrollCinematico(escenaRef, mediaRef);

    const cerrarVisor = useCallback(() => {
        setVisorAbierto(false);
        abrirRef.current?.focus();
    }, []);

    const irAPresentacion = () => {
        mediaRef.current?.scrollIntoView({ behavior: "smooth", block: "center" });
    };

    return (
        <section id="inicio" className="hc">
            {/* ===== 1. Escena a sangre completa con parallax ===== */}
            <div className="hc-escena force-dark" ref={escenaRef}>
                <div className="hc-escena-imagen" role="img" aria-label="Bailarina de Arte y Movimiento en el escenario" />
                <div className="hc-escena-velo" aria-hidden="true" />
                <p className="hc-escena-pie" aria-hidden="true">Academia de danza · Cali, Colombia</p>
            </div>

            {/* ===== 2. Lienzo que sube sobre la escena, con el título ===== */}
            <div className="hc-lienzo">
                <p className="hc-etiqueta">Bienvenido a la academia</p>
                <h1 className="hc-titulo">
                    <span className="hc-titulo-linea"><span>Arte</span></span>
                    <span className="hc-titulo-linea"><span><em>&amp;</em> Movimiento</span></span>
                </h1>
                <p className="hc-lema">
                    El movimiento es nuestra pasión. Descubre el arte de bailar, desarrolla tu
                    talento y forma parte de nuestra comunidad.
                </p>

                <div className="hc-acciones">
                    <Link to="/clases" className="hc-boton hc-boton--lleno">Explorar clases</Link>
                    <a href="#nosotros" className="hc-boton">Un poco de nosotros</a>
                </div>

                <button type="button" className="hc-bajar" onClick={irAPresentacion} aria-label="Bajar a la presentación">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                        <path d="M12 5v14M6 13l6 6 6-6" />
                    </svg>
                </button>
            </div>

            {/* ===== 3. Presentación que crece con el scroll (arco -> marco) ===== */}
            <div className="hc-media-zona">
                <button
                    ref={(nodo) => { mediaRef.current = nodo; abrirRef.current = nodo; }}
                    type="button"
                    className="hc-media"
                    onClick={() => setVisorAbierto(true)}
                    aria-haspopup="dialog"
                >
                    <img src="/images/ImgArco1.jpg" alt="" className="hc-media-imagen" />
                    <span className="hc-media-velo" aria-hidden="true" />
                    <span className="hc-media-centro">
                        <span className="hc-media-play" aria-hidden="true">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M8 5.5v13l11-6.5z" /></svg>
                        </span>
                        <span className="hc-media-texto">
                            <span>Ver nuestra</span>
                            <span>presentación</span>
                        </span>
                    </span>
                </button>
            </div>

            {/* ===== 4. Los tres pilares ===== */}
            <div className="hc-pilares">
                <p className="hc-etiqueta">Lo que nos mueve</p>
                <ul className="hc-pilares-lista">
                    {PILARES.map(({ palabra, detalle }) => (
                        <li key={palabra} className="hc-pilar" tabIndex={0}>
                            <span className="hc-pilar-palabra">{palabra}</span>
                            <span className="hc-pilar-detalle">{detalle}</span>
                        </li>
                    ))}
                </ul>
            </div>

            {visorAbierto && <Visor inicial={0} onCerrar={cerrarVisor} />}
        </section>
    );
}

// Índice final de la página de inicio (estilo "sitemap" editorial)
const ENLACES_INDICE = [
    { to: "/historia", label: "Historia" },
    { to: "/m-v", label: "Misión y visión" },
    { to: "/clases", label: "Clases" },
    { to: "/horario", label: "Horario" },
    { to: "/profesores", label: "Profesores" },
    { to: "/register", label: "Inscríbete" },
];

export function IndiceInicio() {
    return (
        <nav className="hc-indice" aria-label="Índice del sitio">
            <p className="hc-etiqueta">Índice</p>
            <ul>
                {ENLACES_INDICE.map(({ to, label }) => (
                    <li key={to}><Link to={to}>{label}</Link></li>
                ))}
            </ul>
            <p className="hc-indice-copy">
                © {new Date().getFullYear()} Academia Arte y Movimiento · Cali, Colombia
            </p>
        </nav>
    );
}

export default HeroCinematico;
