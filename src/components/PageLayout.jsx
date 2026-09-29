import { useEffect, useState } from "react";
import "../styles/transiciones.css";

function PageLayout({ children }) {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        // Cuando el componente se monta, activa la animación
        setIsVisible(true);

        // Si el componente se desmonta (cambio de página), reinicia
        return () => setIsVisible(false);
    }, []);

    return (
        <div className={isVisible ? "page-transition-enter-active" : "page-transition-enter"}>
            {children}
        </div>
    );
}

export default PageLayout;