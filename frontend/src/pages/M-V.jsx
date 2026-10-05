import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageLayout from "../components/PageLayout";
import "../styles/M-V.css";

function MV() {
  return (
    <>
      <Navbar />
      <PageLayout className="mv-page">
        <section className="mv-section">
          <span className="mv-subtitle">Nuestro Propósito</span>
          <h1> Misión y <span> Visión</span></h1>
        </section>

        <section className="mv-content">
            <article className="mv-card">
                <div className="mv-icon" aria-hidden="true"> </div>
                <h2 className="mv-card-title">Misión</h2>
                <p className="mv-card-description">
                    Arte y Movimiento (a&m) es una academia ubicada en la ciudad  Cali (Colombia), que se enfoca en el desarrollo artístico e integral de sus  estudiantes, a través de técnicas básicas de la danza y el arte, en donde se enfoca en el aprendizaje y desarrollo  corporal humano.
                </p>
            </article>
            <article className="mv-card">
                <div className="mv-icon" aria-hidden="true"> </div>
                <h2 className="mv-card-title">Visión</h2>
                <p className="mv-card-description">
                   En el 2028 Arte y Movimiento (a&m) será reconocida a nivel nacional como una institución artística que fomenta y prepara a sus estudiantes integralmente, respetando sus procesos de formación, aprendizaje y desarrollo corporal.
                </p>
            </article>
        </section>
      </PageLayout>
      <Footer />
    </>
  );
}

export default MV;