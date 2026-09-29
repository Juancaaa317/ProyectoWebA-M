import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageLayout from "../components/PageLayout";
import "../styles/historia.css";

function Historia() {
    return (
        <>
            <Navbar />
            <PageLayout>
                <main className="historia-page">
                    <section className="historia-header">
                        <span className="historia-subtitle">Nuestros Orígenes</span>
                        <h1>Nuestra <span>Historia</span></h1>
                    </section>

                    <section className="historia-content">
                        <div className="historia-texto">
                            <p>
                                Arte y Movimiento nació del amor por el baile. Tras los eventos sociales
                                ocurridos en Cali durante 2021 y un posterior cambio de ubicación, la academia
                                experimentó una reinvención total para seguir inspirando a través del movimiento.
                            </p>
                            <p>
                                Hoy contamos con una sólida base de estudiantes activos. Nos enfocamos
                                en la formación integral a través de la salsa caleña, la danza urbana y la bachata,
                                superando todos los retos administrativos para ofrecer un espacio de calidad.
                            </p>
                        </div>

                        <div className="historia-imagen">
                            <img src="/Images/ImgPresentacion1.jpg" alt="Historia de Arte y Movimiento" />
                        </div>
                    </section>
                </main>
            </PageLayout>
            <Footer />
        </>
    );
}

export default Historia;