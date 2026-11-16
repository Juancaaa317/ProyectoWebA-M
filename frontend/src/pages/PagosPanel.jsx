import { useAuth } from "../context/AuthContext";
import { ROLES } from "../config/roles";
import Pagos from "./Pagos";
import AdminPagos from "./AdminPagos";

// /panel/pagos muestra la pantalla que corresponde al rol
function PagosPanel() {
    const { user } = useAuth();
    return user.rol === ROLES.ADMIN ? <AdminPagos /> : <Pagos />;
}

export default PagosPanel;
