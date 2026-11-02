import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import PanelLayout from "./components/PanelLayout";
import { ROLES } from "./config/roles";
import Home from "./pages/Home";
import Historia from "./pages/Historia";
import Login from "./pages/Login";
import Register from "./pages/Register";
import MV from "./pages/M-V";
import Clases from "./pages/Clases";
import Horario from "./pages/Horario";
import Profesores from "./pages/Profesores";
import Resumen from "./pages/Resumen";
import Perfil from "./pages/Perfil";
import AdminProgramas from "./pages/AdminProgramas";
import AdminClases from "./pages/AdminClases";
import AdminProfesores from "./pages/AdminProfesores";
import AdminEstudiantes from "./pages/AdminEstudiantes";
import Inscripciones from "./pages/Inscripciones";
import Asistencia from "./pages/Asistencia";
import HorarioPanel from "./pages/HorarioPanel";
import SeccionPendiente from "./pages/SeccionPendiente";
import NotFound from "./pages/NotFound";
import ScrollToTop from "./components/ScrollToTop";

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <ScrollToTop />
                <Routes>
                    {/* Rutas públicas */}
                    <Route path="/" element={<Home />} />
                    <Route path="/historia" element={<Historia />} />
                    <Route path="/m-v" element={<MV />} />
                    <Route path="/clases" element={<Clases />} />
                    <Route path="/horario" element={<Horario />} />
                    <Route path="/profesores" element={<Profesores />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* Panel único: el menú cambia según el rol */}
                    <Route
                        path="/panel"
                        element={
                            <PrivateRoute>
                                <PanelLayout />
                            </PrivateRoute>
                        }
                    >
                        <Route index element={<Resumen />} />
                        <Route path="perfil" element={<Perfil />} />
                        <Route
                            path="programas"
                            element={
                                <PrivateRoute roles={[ROLES.ADMIN]}>
                                    <AdminProgramas />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="clases"
                            element={
                                <PrivateRoute roles={[ROLES.ADMIN, ROLES.PROFESOR]}>
                                    <AdminClases />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="profesores"
                            element={
                                <PrivateRoute roles={[ROLES.ADMIN]}>
                                    <AdminProfesores />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="estudiantes"
                            element={
                                <PrivateRoute roles={[ROLES.ADMIN]}>
                                    <AdminEstudiantes />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="inscripciones"
                            element={
                                <PrivateRoute roles={[ROLES.ADMIN, ROLES.ESTUDIANTE]}>
                                    <Inscripciones />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="asistencia"
                            element={
                                <PrivateRoute roles={[ROLES.ADMIN, ROLES.PROFESOR, ROLES.ESTUDIANTE]}>
                                    <Asistencia />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="horario"
                            element={
                                <PrivateRoute roles={[ROLES.PROFESOR, ROLES.ESTUDIANTE]}>
                                    <HorarioPanel />
                                </PrivateRoute>
                            }
                        />
                        {/* Cualquier otra sección del menú del rol */}
                        <Route path=":seccion" element={<SeccionPendiente />} />
                    </Route>

                    <Route path="*" element={<NotFound />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
