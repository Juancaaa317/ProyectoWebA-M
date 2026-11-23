import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function GuestRoute({ children }) {
    const { user } = useAuth();

    if (user) {
        return <Navigate to={user.rol === "ADMIN" ? "/admin" : "/perfil"} replace />;
    }

    return children;
}

export default GuestRoute;
