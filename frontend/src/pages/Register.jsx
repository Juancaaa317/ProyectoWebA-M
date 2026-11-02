import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { useAuth } from "../context/AuthContext";
import "../styles/register.css";

function Register() {
  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [correo, setCorreo] = useState("");
  const [telefono, setTelefono] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (
      !nombre ||
      !apellido ||
      !correo ||
      !telefono ||
      !password ||
      !confirmPassword
    ) {
      setError("Por favor, completa todos los campos.");
      return;
    }

    if (!/^\d+$/.test(telefono)) {
      setError("El teléfono solo debe contener números.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      await register({ nombre, apellido, correo, telefono, password });
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
            <div className="register-row">
              <div className="form-group">
                <label htmlFor="nombre">
                  Nombre
                </label>

                <input
                  type="text"
                  id="nombre"
                  placeholder="Tu nombre"
                  value={nombre}
                  onChange={(event) =>
                    setNombre(event.target.value)
                  }
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label htmlFor="apellido">
                  Apellido
                </label>

                <input
                  type="text"
                  id="apellido"
                  placeholder="Tu apellido"
                  value={apellido}
                  onChange={(event) =>
                    setApellido(event.target.value)
                  }
                  disabled={loading}
                />
              </div>
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
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">
                Contraseña
              </label>

              <input
                type="password"
                id="password"
                placeholder="Crea una contraseña"
                value={password}
                onChange={(event) =>
                  setPassword(event.target.value)
                }
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">
                Confirmar contraseña
              </label>

              <input
                type="password"
                id="confirmPassword"
                placeholder="Repite tu contraseña"
                value={confirmPassword}
                onChange={(event) =>
                  setConfirmPassword(event.target.value)
                }
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
