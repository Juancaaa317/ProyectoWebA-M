import Navbar from "../components/Navbar";
import Hero from "../components/Hero";
import Nosotros from "../components/About";

function Home() {
  return (
    <>
      <Navbar />
      <main>
        <Hero />
        <Nosotros />
      </main>
    </>
  );
}

export default Home;