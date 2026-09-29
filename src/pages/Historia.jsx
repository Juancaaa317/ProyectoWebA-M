import Navbar from "../components/Navbar";
import Footer from "../components/Footer"; // Si tienes un footer, lo importas también

function Historia() {
    return (
        <>
            <Navbar />
            <main style={{ padding: "100px 20px", textAlign: "center" }}>
                <h1>Nuestra Historia</h1>
                <p>
                    Tras los eventos de 2021 en Cali, Arte y Movimiento se reinventó
                    para seguir inspirando a través del baile...
                </p>
            </main>
            <Footer />
        </>
    );
}

export default Historia;