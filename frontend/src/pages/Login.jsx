import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { useAuth } from "../context/AuthContext";
import "../styles/login.css";

function Login() {
  const [correo, setCorreo] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!correo.trim() || !password) {
      setError("Por favor, completa todos los campos.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      const usuario = await login(correo.trim(), password);

      // Si venía de una ruta privada, lo devolvemos ahí.
      // Si no: el admin va a su panel y los demás al inicio.
      const destino =
        location.state?.from?.pathname ??
        (usuario.rol === "ADMIN" ? "/admin" : "/");

      navigate(destino, { replace: true });
    } catch (err) {
      setError(err.message || "No se pudo iniciar sesión.");
    } finally {
      setLoading(false);
    }
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
                disabled={loading}
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
                disabled={loading}
              />
            </div>

            {error && <p className="login-error">{error}</p>}

            <button type="submit" className="login-button" disabled={loading}>
              {loading ? "Ingresando..." : "Iniciar sesión"}
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