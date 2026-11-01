import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

function NotFound() {
    return (
        <>
            <Navbar />
            <main className="notfound">
                <p className="notfound-code">404</p>
                <h1>Página no encontrada</h1>
                <p>La dirección que buscas no existe o fue movida.</p>
                <Link to="/" className="nav-register">
                    Volver al inicio
                </Link>
            </main>
            <Footer />
        </>
    );
}

export default NotFound;