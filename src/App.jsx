import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Historia from "./pages/Historia";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Ruta principal que carga tu Home actual */}
                <Route path="/" element={<Home />} />

                {/* Nueva ruta para la historia de la academia */}
                <Route path="/historia" element={<Historia />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;