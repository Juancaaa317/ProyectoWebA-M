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
                                Fundada en septiembre de 2014, nuestra academia nació para enseñar la técnica clásica en la primera 
                                infancia, respetando el desarrollo de los niños. Con el tiempo, incluimos géneros como Danza Acrobática,
                                Contemporánea, Jazz, Ritmos Latinos y Urbanos, promoviendo la disciplina de la danza en todas las edades.
                            </p>
                            <p>
                                Promovemos la danza como una herramienta fundamental para fortalecer el desarrollo motor, corporal, 
                                emocional, creativo y social, mediante experiencias de aprendizaje que favorecen la coordinación, 
                                el equilibrio, la expresión corporal, la disciplina, la autonomía y la confianza en sí mismos.
                            </p>
                        </div>

                        <div className="historia-imagen">
                            <img src="/images/ImgArco1.jpg" alt="Historia de Arte y Movimiento" />
                        </div>
                    </section>
                </main>
            </PageLayout>
            <Footer />
        </>
    );
}

export default Historia;