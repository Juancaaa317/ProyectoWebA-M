# Guía de ejecución y verificación local

Esta guía corresponde al frontend y backend actuales. El registro, el inicio de sesión y los módulos conectados consumen la API real; no dependen de autenticación simulada ni de datos de prueba locales.

## Iniciar la aplicación

1. Configura `backend/.env` usando `backend/.env.example` como referencia.
2. Inicia MySQL. En una terminal, entra a `backend` y ejecuta `.\mvnw.cmd spring-boot:run`.
3. En otra terminal, entra a `frontend` y ejecuta `npm run dev`.
4. Abre la dirección que muestra Vite, normalmente `http://localhost:5173`.
5. Para consultar o ejecutar las rutas desde Swagger UI, abre `http://localhost:8080/docs`. La guía completa está en [backend/API.md](backend/API.md).

## Módulos conectados desde el panel

- `/register`: registra una cuenta de estudiante contra `/api/auth/register` y luego inicia sesión.
- `/login`: valida las credenciales contra `/api/auth/login`.
- `/panel/programas`: el administrador puede listar, crear, editar y eliminar programas.
- `/panel/clases`: lista clases; el administrador puede crear, editar y eliminar clases, gestionar sus horarios y programar sesiones.
- `/panel/profesores`: el administrador puede consultar profesores y registrar uno nuevo.
- `/panel/estudiantes`: el administrador puede consultar y filtrar estudiantes.
- `/panel/inscripciones`: estudiantes solicitan cupo y consultan sus solicitudes; el administrador las revisa y cambia su estado.
- `/panel/asistencia`: estudiantes consultan su historial; profesores consultan sesiones y registran asistencia.
- `/panel/horario`: estudiantes y profesores consultan horarios según su rol.
- `/panel/pagos`: estudiantes registran pagos y consultan el historial; administradores filtran, abren comprobantes y los aprueban o rechazan.
- `/panel/perfil`: usuarios autenticados consultan y actualizan su nombre/teléfono y pueden cambiar su contraseña.
- Sin sesión, `/panel` y sus rutas deben redirigir al login.

Las rutas y permisos del servidor también se aplican aunque se escriba directamente la dirección en el navegador. Swagger UI está disponible en `http://localhost:8080/docs` cuando el backend está iniciado.

## Pruebas de errores con Postman

- **400 Validación:** envía a `POST /api/auth/register` un teléfono con letras o contraseñas distintas.
- **401 Credenciales:** envía credenciales incorrectas a `POST /api/auth/login`.
- **401 Sin sesión:** llama a una operación protegida, por ejemplo `POST /api/programas`, sin token.
- **403 Rol insuficiente:** repite `POST /api/programas` con token de estudiante.
- **404 No encontrado:** consulta `GET /api/programas/999999`.
- **409 Duplicado:** intenta registrar otra vez un correo existente o crear un programa con nombre repetido.
- **Error de conexión:** detén el backend y vuelve a cargar `/panel/programas`; debe aparecer la opción de reintentar.

Para probar operaciones administrativas, inicia sesión con un usuario que tenga rol `ADMINISTRADOR`. Los errores de aplicación y de seguridad usan el formato común `ApiErrorDTO`. La guía de API contiene el catálogo de rutas, payloads, códigos y permisos.

## Alcance de estas pruebas

La guía describe las pantallas conectadas según el código actual. El detalle de endpoints y las instrucciones de Swagger están en [backend/API.md](backend/API.md). Antes de la entrega, ejecuta las comprobaciones automatizadas y realiza la demo con la base de datos y ambos servidores iniciados. No uses un conteo antiguo de pruebas/endpoints: reporta los resultados de la ejecución que hagas para la versión que entregarás.
