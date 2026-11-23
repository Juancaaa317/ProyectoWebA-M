import { useEffect, useId, useRef, useState } from "react";
import "../styles/zonaComprobante.css";

const TIPOS_ACEPTADOS = ".pdf,.png,.jpg,.jpeg,application/pdf,image/png,image/jpeg";

function formatearTamano(bytes) {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${Math.round(bytes / 1024)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

// Zona para soltar o elegir el comprobante. Con un archivo elegido se convierte en su vista previa.
// La validación del archivo la hace quien la usa (onElegir recibe el archivo; error es el mensaje a mostrar).
function ZonaComprobante({ archivo, onElegir, onQuitar, onRechazar, error, disabled = false }) {
    const inputRef = useRef(null);
    const idInput = useId();
    const idError = useId();
    const [arrastrando, setArrastrando] = useState(false);
    const [vista, setVista] = useState({ archivo: null, url: null });

    // Miniatura solo para imágenes. Se guarda junto al archivo al que pertenece para ignorar la de uno anterior
    useEffect(() => {
        if (!archivo || !archivo.type.startsWith("image/")) return undefined;
        const lector = new FileReader();
        lector.onload = () => setVista({ archivo, url: lector.result });
        lector.readAsDataURL(archivo);
        return () => lector.abort();
    }, [archivo]);

    const miniatura = vista.archivo === archivo ? vista.url : null;

    const elegir = (evento) => {
        const elegido = evento.target.files?.[0];
        if (elegido) onElegir(elegido);
        evento.target.value = ""; // permite volver a elegir el mismo archivo
    };

    const soltar = (evento) => {
        evento.preventDefault();
        setArrastrando(false);
        if (disabled) return;
        const soltados = evento.dataTransfer.files;
        if (soltados.length > 1) {
            onRechazar("Soltaste varios archivos. Suelta solo el comprobante.");
            return;
        }
        if (soltados[0]) onElegir(soltados[0]);
    };

    const sobrevolar = (evento) => {
        evento.preventDefault();
        if (!disabled) setArrastrando(true);
    };

    const salir = (evento) => {
        if (!evento.currentTarget.contains(evento.relatedTarget)) setArrastrando(false);
    };

    return (
        <div
            className={`zona-comprobante${disabled ? " zona-comprobante--ocupada" : ""}`}
            onDragOver={sobrevolar}
            onDragLeave={salir}
            onDrop={soltar}
        >
            <input
                ref={inputRef}
                id={idInput}
                type="file"
                className="zona-comprobante-input"
                accept={TIPOS_ACEPTADOS}
                onChange={elegir}
                disabled={disabled}
                aria-label="Elegir comprobante de pago"
                tabIndex={archivo ? -1 : undefined}
                aria-describedby={error ? idError : undefined}
            />

            {archivo ? (
                <div className="zona-previa">
                    <span className="zona-miniatura" aria-hidden="true">
                        {miniatura ? (
                            <img src={miniatura} alt="" />
                        ) : (
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6"
                                strokeLinecap="round" strokeLinejoin="round">
                                <path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z" />
                                <path d="M14 3v5h5M9 13h6M9 17h6" />
                            </svg>
                        )}
                    </span>
                    <span className="zona-datos">
                        <strong className="zona-nombre">{archivo.name}</strong>
                        <span className="zona-tamano">{formatearTamano(archivo.size)}</span>
                    </span>
                    <span className="zona-acciones">
                        <button type="button" className="admin-btn-cancelar" disabled={disabled}
                            onClick={() => inputRef.current?.click()}>
                            Cambiar
                        </button>
                        <button type="button" className="admin-btn-cancelar" disabled={disabled}
                            onClick={onQuitar}>
                            Quitar
                        </button>
                    </span>
                </div>
            ) : (
                <label
                    htmlFor={idInput}
                    className={`zona-soltar${arrastrando ? " zona-soltar--activa" : ""}`}
                >
                    {arrastrando ? (
                        <span className="zona-titulo">Suéltalo aquí</span>
                    ) : (
                        <>
                            <span className="zona-titulo">
                                <span className="zona-arrastra">Arrastra tu comprobante aquí</span>
                                <span className="zona-toca">Toca para elegir tu comprobante</span>
                            </span>
                            <span className="zona-ayuda zona-arrastra">o haz clic para elegirlo</span>
                        </>
                    )}
                    <span className="zona-formatos">PDF, PNG o JPG. Máximo 5 MB.</span>
                </label>
            )}

            {error && <p id={idError} className="login-error" role="alert">{error}</p>}
        </div>
    );
}

export default ZonaComprobante;
