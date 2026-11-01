import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function PrivateRoute({ children, roles }) {
    const { user } = useAuth();
    const location = useLocation();

    // Sin sesión: al login, recordando a dónde quería ir
    if (!user) {
        return <Navigate to="/login" replace state={{ from: location }} />;
    }

    // Con sesión pero sin el rol requerido: al inicio
    if (roles && !roles.includes(user.rol)) {
        return <Navigate to="/" replace />;
    }

    return children;
}

export default PrivateRoute;