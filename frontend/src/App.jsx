import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import AdminLayout from "./components/AdminLayout";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Historia from "./pages/Historia";
import Login from "./pages/Login";
import Register from "./pages/Register";
import MV from "./pages/M-V";
import Clases from "./pages/Clases";
import Horario from "./pages/Horario";
import Profesores from "./pages/Profesores";
import Perfil from "./pages/Perfil";
import AdminProgramas from "./pages/AdminProgramas";
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

                    {/* Perfil para usuarios que no son admin (luego tendrán su propio layout) */}
                    <Route
                        path="/perfil"
                        element={
                            <PrivateRoute>
                                <>
                                    <Navbar />
                                    <main>
                                        <Perfil />
                                    </main>
                                    <Footer />
                                </>
                            </PrivateRoute>
                        }
                    />

                    {/* Panel de administración (solo ADMIN) */}
                    <Route
                        path="/admin"
                        element={
                            <PrivateRoute roles={["ADMIN"]}>
                                <AdminLayout />
                            </PrivateRoute>
                        }
                    >
                        <Route index element={<Navigate to="programas" replace />} />
                        <Route path="programas" element={<AdminProgramas />} />
                        <Route path="perfil" element={<Perfil />} />
                    </Route>

                    <Route path="*" element={<NotFound />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;