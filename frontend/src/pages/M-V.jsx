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
                    Nuestra misión es brindar una educación artística de calidad, fomentando la creatividad, la expresión personal y el desarrollo integral de nuestros estudiantes. Nos comprometemos a ofrecer un entorno inclusivo y estimulante que inspire a cada individuo a alcanzar su máximo potencial.
                </p>
            </article>
            <article className="mv-card">
                <div className="mv-icon" aria-hidden="true"> </div>
                <h2 className="mv-card-title">Visión</h2>
                <p className="mv-card-description">
                    Nuestra visión es ser una institución reconocida por su excelencia en la educación artística, formando ciudadanos críticos, creativos y comprometidos con el desarrollo cultural de su comunidad.
                </p>
            </article>
        </section>
      </PageLayout>
      <Footer />
    </>
  );
}

export default MV;