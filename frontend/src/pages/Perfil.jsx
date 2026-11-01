import { useAuth } from "../context/AuthContext";
import "../styles/perfil.css";

function Perfil() {
    const { user } = useAuth();

    return (
        <div className="perfil">
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

                    <dt>ID de usuario</dt>
                    <dd>{user.id}</dd>
                </dl>
            </div>
        </div>
    );
}

export default Perfil;