import { useEffect, useId, useRef, useState } from "react";
import "../styles/selectorFecha.css";

// Selector de fecha propio (reemplaza al <input type="date"> nativo).
// Trabaja con cadenas "AAAA-MM-DD", igual que el input nativo y la API.

const MESES = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
    "agosto", "septiembre", "octubre", "noviembre", "diciembre"];
const DIAS_CORTOS = ["Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do"];
const DIAS_LARGOS = ["lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"];
// Mismo orden que el enum DiaSemana del backend
const DIAS_API = ["LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"];

const dos = (n) => String(n).padStart(2, "0");
const aTexto = (anio, mes, dia) => `${anio}-${dos(mes + 1)}-${dos(dia)}`;

const desdeTexto = (texto) => {
    const [anio, mes, dia] = texto.split("-").map(Number);
    return { anio, mes: mes - 1, dia };
};

const hoyTexto = () => {
    const d = new Date();
    return aTexto(d.getFullYear(), d.getMonth(), d.getDate());
};

// Suma días a una fecha "AAAA-MM-DD" sin problemas de zona horaria
const sumarDias = (texto, dias) => {
    const { anio, mes, dia } = desdeTexto(texto);
    const d = new Date(anio, mes, dia + dias);
    return aTexto(d.getFullYear(), d.getMonth(), d.getDate());
};

// 0 = lunes … 6 = domingo
const diaDeSemana = (texto) => {
    const { anio, mes, dia } = desdeTexto(texto);
    return (new Date(anio, mes, dia).getDay() + 6) % 7;
};

const textoLargo = (texto) => {
    const { anio, mes, dia } = desdeTexto(texto);
    return `${DIAS_LARGOS[diaDeSemana(texto)]}, ${dia} de ${MESES[mes]} de ${anio}`;
};

function SelectorFecha({
    value,
    onChange,
    min,
    max,
    diaSemana,            // opcional: "LUNES" … "DOMINGO"; solo se permiten esos días
    placeholder = "Elige una fecha",
    disabled = false,
    etiquetaId,           // id del texto que nombra el campo (accesibilidad)
}) {
    const [abierto, setAbierto] = useState(false);
    const [vista, setVista] = useState(() => desdeTexto(value || hoyTexto()));
    const [enfocada, setEnfocada] = useState(value || hoyTexto());
    const contenedorRef = useRef(null);
    const botonRef = useRef(null);
    const gridRef = useRef(null);
    // Solo se lleva el foco a la cuadrícula al abrir o al moverse con el teclado
    const enfocarGrid = useRef(false);
    const idPanel = useId();

    const indiceDiaPermitido = diaSemana ? DIAS_API.indexOf(diaSemana) : -1;

    const esValida = (texto) =>
        (!min || texto >= min) &&
        (!max || texto <= max) &&
        (indiceDiaPermitido < 0 || diaDeSemana(texto) === indiceDiaPermitido);

    const abrir = () => {
        // Arranca en la fecha elegida, o en hoy ajustado al rango de la clase
        let inicial = value || hoyTexto();
        if (!value && min && inicial < min) inicial = min;
        if (!value && max && inicial > max) inicial = max;
        // Sin fecha elegida, se posiciona en el primer día que sí se puede escoger
        if (!value) {
            for (let i = 0, f = inicial; i < 366; i++, f = sumarDias(f, 1)) {
                if (max && f > max) break;
                if (esValida(f)) { inicial = f; break; }
            }
        }
        setVista(desdeTexto(inicial));
        setEnfocada(inicial);
        enfocarGrid.current = true;
        setAbierto(true);
    };

    const cerrar = (devolverFoco = true) => {
        setAbierto(false);
        if (devolverFoco) botonRef.current?.focus();
    };

    const elegir = (texto) => {
        if (!esValida(texto)) return;
        onChange(texto);
        cerrar();
    };

    // Cierra al hacer clic fuera
    useEffect(() => {
        if (!abierto) return;
        const fuera = (e) => {
            if (!contenedorRef.current?.contains(e.target)) setAbierto(false);
        };
        document.addEventListener("mousedown", fuera);
        return () => document.removeEventListener("mousedown", fuera);
    }, [abierto]);

    // Mueve el foco del teclado al día enfocado
    useEffect(() => {
        if (!abierto || !enfocarGrid.current) return;
        enfocarGrid.current = false;
        gridRef.current?.querySelector(`[data-fecha="${enfocada}"]`)?.focus();
    }, [abierto, enfocada, vista]);

    const moverFoco = (texto) => {
        enfocarGrid.current = true;
        setEnfocada(texto);
        const { anio, mes } = desdeTexto(texto);
        if (anio !== vista.anio || mes !== vista.mes) setVista({ anio, mes, dia: 1 });
    };

    const alTeclear = (e) => {
        const pasos = { ArrowLeft: -1, ArrowRight: 1, ArrowUp: -7, ArrowDown: 7 };
        if (e.key in pasos) {
            e.preventDefault();
            moverFoco(sumarDias(enfocada, pasos[e.key]));
        } else if (e.key === "Escape") {
            e.preventDefault();
            cerrar();
        }
    };

    const cambiarMes = (delta) => {
        const d = new Date(vista.anio, vista.mes + delta, 1);
        setVista({ anio: d.getFullYear(), mes: d.getMonth(), dia: 1 });
        setEnfocada(aTexto(d.getFullYear(), d.getMonth(), 1));
    };

    // ¿Se puede ir al mes anterior / siguiente sin salir del rango?
    const primeroDeVista = aTexto(vista.anio, vista.mes, 1);
    const ultimoDeVista = aTexto(vista.anio, vista.mes, new Date(vista.anio, vista.mes + 1, 0).getDate());
    const hayAnterior = !min || min < primeroDeVista;
    const haySiguiente = !max || max > ultimoDeVista;

    // Celdas del mes: huecos iniciales para que la semana empiece en lunes
    const diasDelMes = new Date(vista.anio, vista.mes + 1, 0).getDate();
    const huecos = diaDeSemana(primeroDeVista);
    const celdas = [
        ...Array.from({ length: huecos }, () => null),
        ...Array.from({ length: diasDelMes }, (_, i) => aTexto(vista.anio, vista.mes, i + 1)),
    ];
    const semanas = [];
    for (let i = 0; i < celdas.length; i += 7) semanas.push(celdas.slice(i, i + 7));

    const hoy = hoyTexto();

    return (
        <div className="sf" ref={contenedorRef}>
            <button
                ref={botonRef}
                type="button"
                className={`sf-boton${value ? " sf-boton--lleno" : ""}`}
                onClick={() => (abierto ? cerrar() : abrir())}
                aria-haspopup="dialog"
                aria-expanded={abierto}
                aria-controls={abierto ? idPanel : undefined}
                aria-labelledby={etiquetaId ? `${etiquetaId} ${idPanel}-texto` : undefined}
                disabled={disabled}
            >
                <span className="sf-boton-icono" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                        <rect x="3" y="5" width="18" height="16" rx="3" />
                        <path d="M8 3v4m8-4v4M3 10h18" />
                    </svg>
                </span>
                <span id={`${idPanel}-texto`} className="sf-boton-texto">{value ? textoLargo(value) : placeholder}</span>
                <span className="sf-boton-flecha" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="m6 9 6 6 6-6" />
                    </svg>
                </span>
            </button>

            {abierto && (
                <div id={idPanel} className="sf-panel" role="dialog" aria-label="Elegir fecha">
                    <div className="sf-cabecera">
                        <button type="button" className="sf-nav" onClick={() => cambiarMes(-1)} disabled={!hayAnterior} aria-label="Mes anterior">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m15 6-6 6 6 6" /></svg>
                        </button>
                        <p className="sf-mes" aria-live="polite">
                            <span>{MESES[vista.mes]}</span> {vista.anio}
                        </p>
                        <button type="button" className="sf-nav" onClick={() => cambiarMes(1)} disabled={!haySiguiente} aria-label="Mes siguiente">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m9 6 6 6-6 6" /></svg>
                        </button>
                    </div>

                    {indiceDiaPermitido >= 0 && (
                        <p className="sf-aviso">
                            {/* "lunes"…"viernes" no cambian en plural; "sábado(s)", "domingo(s)" sí */}
                            Solo {DIAS_LARGOS[indiceDiaPermitido].endsWith("s")
                                ? DIAS_LARGOS[indiceDiaPermitido]
                                : `${DIAS_LARGOS[indiceDiaPermitido]}s`}, según el horario elegido.
                        </p>
                    )}

                    <table className="sf-grid" role="grid" ref={gridRef} onKeyDown={alTeclear}>
                        <thead>
                            <tr>
                                {DIAS_CORTOS.map((d, i) => (
                                    <th key={d} scope="col" abbr={DIAS_LARGOS[i]}
                                        className={i === indiceDiaPermitido ? "sf-col-activa" : undefined}>
                                        {d}
                                    </th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            {semanas.map((semana, s) => (
                                <tr key={s}>
                                    {semana.map((fecha, d) => {
                                        if (!fecha) return <td key={`v${d}`} />;
                                        const valida = esValida(fecha);
                                        const clases = ["sf-dia"];
                                        if (fecha === value) clases.push("sf-dia--elegido");
                                        if (fecha === hoy) clases.push("sf-dia--hoy");
                                        if (valida) clases.push("sf-dia--valido");
                                        return (
                                            <td key={fecha}>
                                                <button
                                                    type="button"
                                                    data-fecha={fecha}
                                                    className={clases.join(" ")}
                                                    tabIndex={fecha === enfocada ? 0 : -1}
                                                    aria-disabled={!valida}
                                                    aria-pressed={fecha === value}
                                                    aria-label={textoLargo(fecha) + (fecha === hoy ? " (hoy)" : "")}
                                                    onClick={() => elegir(fecha)}
                                                >
                                                    {desdeTexto(fecha).dia}
                                                </button>
                                            </td>
                                        );
                                    })}
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div className="sf-pie">
                        <button type="button" className="sf-accion" onClick={() => elegir(hoy)} disabled={!esValida(hoy)}>
                            Hoy
                        </button>
                        {value && (
                            <button type="button" className="sf-accion sf-accion--suave" onClick={() => { onChange(""); cerrar(); }}>
                                Borrar
                            </button>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default SelectorFecha;
