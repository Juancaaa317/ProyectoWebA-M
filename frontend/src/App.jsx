import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Home from "./pages/Home";
import Historia from "./pages/Historia";
import Login from "./pages/Login";
import Register from "./pages/Register";
import MV from "./pages/M-V";
import Clases from "./pages/Clases";
import Horario from "./pages/Horario";
import Profesores from "./pages/Profesores";
import NotFound from "./pages/NotFound";
import ScrollToTop from "./components/ScrollToTop";

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <ScrollToTop />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/historia" element={<Historia />} />
                    <Route path="/m-v" element={<MV />} />
                    <Route path="/clases" element={<Clases />} />
                    <Route path="/horario" element={<Horario />} />
                    <Route path="/profesores" element={<Profesores />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="*" element={<NotFound />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;