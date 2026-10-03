import { useState } from "react";
import Navbar from "../components/Navbar";
import "../styles/register.css";

function Register() {
    const [nombre, setNombre] = useState("");
    const [apellido, setApellido] = useState("");
    const [correo, setCorreo] = useState("");
    const [telefono, setTelefono] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");

 const handleSubmit = (event) => {
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
    console.log("Formulario de registro listo para enviar");
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
                            Regístrate para acceder a los servicios de la
                            academia y gestionar tus actividades.
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
                        >
                            Crear cuenta
                        </button>
                    </form>

                    <p className="register-login">
                        ¿Ya tienes una cuenta?
                        <a href="/login"> Inicia sesión</a>
                    </p>

                </section>
            </main>
        </>
    );
}

export default Register;