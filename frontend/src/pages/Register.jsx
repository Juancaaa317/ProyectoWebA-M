import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { useAuth } from "../context/AuthContext";
import "../styles/register.css";

function Register() {
  const [nombre, setNombre] = useState("");
  const [correo, setCorreo] = useState("");
  const [telefono, setTelefono] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [confirmarContrasena, setConfirmarContrasena] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { register, login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (
      !nombre.trim() ||
      !correo ||
      !telefono ||
      !contrasena ||
      !confirmarContrasena
    ) {
      setError("Por favor, completa todos los campos.");
      return;
    }

    if (!/^\d{7,15}$/.test(telefono.trim())) {
      setError("El teléfono debe tener entre 7 y 15 dígitos.");
      return;
    }

    if (contrasena !== confirmarContrasena) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      await register({
        nombre: nombre.trim(),
        telefono: telefono.trim(),
        correo: correo.trim().toLowerCase(),
        contrasena,
        confirmarContrasena,
      });
      await login(correo.trim().toLowerCase(), contrasena);
      navigate("/panel", { replace: true });
    } catch (err) {
      setError(err.message || "No se pudo crear la cuenta.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />

      <main className="register-page">
        <section className="register-card">
          <div className="register-header">
            <p className="register-subtitle">
              ARTE & MOVIMIENTO
            </p>

            <h1>Crear una cuenta</h1>

            <p className="register-description">
              Regístrate para acceder a los servicios de la academia y
              gestionar tus actividades.
            </p>
          </div>

          <form
            className="register-form"
            onSubmit={handleSubmit}
          >
            <div className="form-group">
              <label htmlFor="nombre">Nombre completo</label>
              <input
                type="text"
                id="nombre"
                placeholder="Tu nombre completo"
                value={nombre}
                onChange={(event) => setNombre(event.target.value)}
                maxLength={150}
                autoComplete="name"
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="correo">
                Correo electrónico
              </label>

              <input
                type="email"
                id="correo"
                placeholder="correo@ejemplo.com"
                value={correo}
                onChange={(event) =>
                  setCorreo(event.target.value)
                }
                autoComplete="email"
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="telefono">
                Teléfono
              </label>

              <input
                type="tel"
                id="telefono"
                placeholder="300 123 4567"
                value={telefono}
                onChange={(event) =>
                  setTelefono(event.target.value)
                }
                inputMode="numeric"
                maxLength={15}
                autoComplete="tel"
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="contrasena">
                Contraseña
              </label>

              <input
                type="password"
                id="contrasena"
                placeholder="Crea una contraseña"
                value={contrasena}
                onChange={(event) =>
                  setContrasena(event.target.value)
                }
                minLength={8}
                maxLength={72}
                autoComplete="new-password"
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="confirmarContrasena">
                Confirmar contraseña
              </label>

              <input
                type="password"
                id="confirmarContrasena"
                placeholder="Repite tu contraseña"
                value={confirmarContrasena}
                onChange={(event) =>
                  setConfirmarContrasena(event.target.value)
                }
                minLength={8}
                maxLength={72}
                autoComplete="new-password"
                required
                disabled={loading}
              />
            </div>

            {error && (
              <p className="register-error">
                {error}
              </p>
            )}

            <button
              type="submit"
              className="register-button"
              disabled={loading}
            >
              {loading ? "Creando cuenta..." : "Crear cuenta"}
            </button>
          </form>

          <p className="register-login">
            ¿Ya tienes una cuenta?
            <Link to="/login"> Inicia sesión</Link>
          </p>
        </section>
      </main>
    </>
  );
}

export default Register;
