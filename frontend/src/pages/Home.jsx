import Navbar from "../components/Navbar";
import Hero from "../components/Hero";
import HeroCinematico, { IndiceInicio } from "../components/HeroCinematico";
import Nosotros from "../components/About";
import PageLayout from "../components/PageLayout";

// Portada de la página de inicio:
//   true  -> versión cinemática (HeroCinematico.jsx + heroCinematico.css)
//   false -> versión anterior   (Hero.jsx + hero.css), que se conserva sin cambios
const USAR_HERO_CINEMATICO = true;

function Home() {
  return (
    <>
      <Navbar />
      <PageLayout>
        <main>
          {USAR_HERO_CINEMATICO ? <HeroCinematico /> : <Hero />}
          <Nosotros />
          {USAR_HERO_CINEMATICO && <IndiceInicio />}
        </main>
      </PageLayout>
    </>
  );
}

export default Home;
