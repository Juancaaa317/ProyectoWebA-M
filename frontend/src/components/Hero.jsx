
import "../styles/hero.css";

function Hero() {
  return (
    <section id="inicio" className="hero">
      <div className="hero-overlay"></div>

      <div className="hero-content">
        <span className="hero-label">
          BIENVENIDO A ARTE Y MOVIMIENTO
        </span>

        <h1>
          El movimiento es
          <span> nuestra pasión.</span>
        </h1>

        <p>
          Descubre el arte de bailar, desarrolla tu
          talento y forma parte de nuestra comunidad.
        </p>

        <div className="hero-buttons">
          <a href="#clases" className="hero-primary">
            Explorar clases
          </a>

          <a href="#nosotros" className="hero-secondary">
            Un poco de nosotros
          </a>
        </div>
      </div>
    </section>
  );
}

export default Hero;