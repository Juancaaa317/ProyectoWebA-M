# Guía de verificación del Avance 2

Esta guía corresponde al frontend y backend actuales. El frontend ya no usa autenticación ni programas simulados.

## Iniciar la aplicación

1. Configura `backend/.env` usando `backend/.env.example` como referencia.
2. Inicia MySQL y ejecuta el backend desde la carpeta `backend`.
3. En otra terminal, inicia el frontend desde `frontend` con `npm run dev`.
4. Abre la dirección que muestra Vite, normalmente `http://localhost:5173`.

## Pruebas desde la página

- `/register`: registra una cuenta de estudiante contra `/api/auth/register` y luego inicia sesión.
- `/login`: valida las credenciales contra `/api/auth/login`.
- `/panel/programas`: permite al administrador listar, crear, editar y eliminar programas.
- Sin sesión, `/panel` y sus rutas deben redirigir al login.
- Las demás secciones del panel aún no tienen interfaz conectada a sus endpoints; por ahora se prueban en Postman.

## Pruebas de errores con Postman

- **400 Validación:** envía a `POST /api/auth/register` un teléfono con letras o contraseñas distintas.
- **401 Credenciales:** envía credenciales incorrectas a `POST /api/auth/login`.
- **401 Sin sesión:** llama a una operación protegida, por ejemplo `POST /api/programas`, sin token.
- **403 Rol insuficiente:** repite `POST /api/programas` con token de estudiante.
- **404 No encontrado:** consulta `GET /api/programas/999999`.
- **409 Duplicado:** intenta registrar otra vez un correo existente o crear un programa con nombre repetido.
- **Error de conexión:** detén el backend y vuelve a cargar `/panel/programas`; debe aparecer la opción de reintentar.

Para probar operaciones administrativas, inicia sesión con un usuario que tenga rol `ADMINISTRADOR`. Las respuestas de validación y negocio incluyen el formato común de `ApiErrorDTO`; algunos errores de seguridad (`401` y `403`) pueden ser generados por Spring Security con un formato distinto.

## Endpoints que tienen interfaz

Por ahora, el CRUD de programas es el único módulo de administración conectado desde una página. Clases, horarios, inscripciones y asistencia cuentan con endpoints en el backend, pero necesitan sus pantallas antes de probarse desde el panel.
