import Navbar from "../components/Navbar";
import Hero from "../components/Hero";
import Nosotros from "../components/About";
import PageLayout from "../components/PageLayout";

function Home() {
  return (
    <>
      <Navbar />
      <PageLayout>
        <main>
          <Hero />
          <Nosotros />
        </main>
      </PageLayout>
    </>
  );
}

export default Home;