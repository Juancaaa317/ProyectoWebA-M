import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import { useAuth } from "../context/AuthContext";
import "../styles/perfil.css";

function Perfil() {
    const { user } = useAuth();

    return (
        <>
            <Navbar />

            <main className="perfil">
                <div className="perfil-card">
                    <div className="perfil-avatar">
                        {user.correo.charAt(0).toUpperCase()}
                    </div>

                    <h1>Mi perfil</h1>

                    <dl>
                        <dt>Correo</dt>
                        <dd>{user.correo}</dd>

                        <dt>Rol</dt>
                        <dd>{user.rol}</dd>
                    </dl>
                </div>
            </main>

            <Footer />
        </>
    );
}

export default Perfil;