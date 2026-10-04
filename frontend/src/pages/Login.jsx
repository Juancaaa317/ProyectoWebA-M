import { useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import "../styles/login.css";

function Login() {
  const [correo, setCorreo] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!correo || !password) {
      setError("Por favor, completa todos los campos.");
      return;
    }

    setError("");
    console.log("Formulario listo para enviar");
  };

  return (
    <>
      <Navbar />

      <main className="login-page">
        <section className="login-card">
          <div className="login-header">
            <p className="login-subtitle">ARTE & MOVIMIENTO</p>

            <h1>Bienvenido de nuevo</h1>

            <p className="login-description">
              Inicia sesión para acceder a tu cuenta y continuar con tu
              experiencia en la academia.
            </p>
          </div>

          <form className="login-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="correo">Correo electrónico</label>

              <input
                type="email"
                id="correo"
                placeholder="correo@ejemplo.com"
                value={correo}
                onChange={(event) => setCorreo(event.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Contraseña</label>

              <input
                type="password"
                id="password"
                placeholder="Ingresa tu contraseña"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </div>

            {error && (
              <p className="login-error">
                {error}
              </p>
            )}

            <button
              type="submit"
              className="login-button"
            >
              Iniciar sesión
            </button>
          </form>

          <p className="login-register">
            ¿No tienes una cuenta?
            <Link to="/register"> Regístrate</Link>
          </p>
        </section>
      </main>
    </>
  );
}

export default Login;