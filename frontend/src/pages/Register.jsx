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
      !nombre.trim() ||
      !apellido.trim() ||
      !correo.trim() ||
      !telefono.trim() ||
      !password ||
      !confirmPassword
    ) {
      setError("Por favor, completa todos los campos.");
      return;
    }

    if (!/^[+\d\s()-]{7,20}$/.test(telefono) || !/\d{7,}/.test(telefono)) {
      setError("Ingresa un teléfono válido.");
      return;
    }

    if (password.length < 8) {
      setError("La contraseña debe tener al menos 8 caracteres.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      await register({
        nombre: nombre.trim(),
        apellido: apellido.trim(),
        correo: correo.trim().toLowerCase(),
        telefono: telefono.trim(),
        password,
      });
      navigate("/perfil", { replace: true });
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
                  autoComplete="given-name"
                  required
                  disabled={loading}
                  onChange={(event) =>
                    setNombre(event.target.value)
                  }
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
                  autoComplete="family-name"
                  required
                  disabled={loading}
                  onChange={(event) =>
                    setApellido(event.target.value)
                  }
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
                autoComplete="email"
                required
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
                autoComplete="tel"
                required
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
                autoComplete="new-password"
                minLength={8}
                required
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
                autoComplete="new-password"
                minLength={8}
                required
                onChange={(event) =>
                  setConfirmPassword(event.target.value)
                }
                disabled={loading}
              />
            </div>

            {error && (
              <p className="register-error" role="alert">
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