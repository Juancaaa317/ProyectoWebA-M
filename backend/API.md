# Documentación de la API

La API se documenta con OpenAPI y Swagger UI a partir de los controladores y DTO de Spring. Las anotaciones de cada operación se mantienen junto a su endpoint para que la descripción no se separe del código que implementa la ruta.

## Abrir y probar la documentación

Con el backend iniciado en el puerto 8080:

- Swagger UI: <http://localhost:8080/docs>
- Especificación OpenAPI JSON: <http://localhost:8080/docs.json>

Para probar rutas protegidas:

1. Ejecuta `POST /api/auth/login` desde Swagger UI con el correo y la contraseña.
2. Copia el valor `token` de la respuesta.
3. Pulsa **Authorize** y pega el token sin escribir `Bearer`.
4. Ejecuta las operaciones habilitadas para el rol de esa cuenta.

El login devuelve el JWT. Los endpoints de administración, estudiantes y profesores mantienen sus permisos en Spring Security; Swagger UI no los cambia.

## Organización

- `config/ApiDocumentationConfig.java`: título, versión, descripción, etiquetas y esquema JWT Bearer.
- `controller/`: rutas REST y anotaciones OpenAPI de resumen, permisos, parámetros y respuestas.
- `dto/`: modelos de solicitud y respuesta usados como esquemas en la documentación.
- `config/SecurityConfig.java`: permite abrir Swagger UI y su JSON; los endpoints de negocio siguen sujetos a sus permisos.

No se mantiene un archivo OpenAPI paralelo a mano: la especificación se genera desde las rutas reales del backend.

## Grupos de rutas

| Grupo | Rutas principales | Acceso |
|---|---|---|
| Autenticación | `/api/auth/register`, `/api/auth/login` | Público |
| Usuarios | `/api/usuarios/estudiantes`, `/api/usuarios/profesores` | Administración |
| Programas | `/api/programas` y operaciones por ID/estado | Lectura pública; escritura de administración |
| Clases | `/api/clases` y operaciones por ID | Lectura pública; escritura de administración |
| Horarios | `/api/horarios` y `/api/horarios/clase/{idClase}` | Lectura pública; escritura de administración |
| Sesiones | `/api/sesiones/clase/{idClase}`, creación y cambio de estado | Lectura pública; escritura de administración |
| Inscripciones | `/api/inscripciones`, `/mias` y cambio de estado | Estudiante o administración según la operación |
| Asistencias | `/api/asistencias/mias`, `/api/sesiones/{idSesion}/asistencias` | Estudiante, profesor asignado o administración según la operación |

## Respuestas HTTP

- `200 OK`: consulta o actualización exitosa.
- `201 Created`: registro creado.
- `204 No Content`: eliminación exitosa sin cuerpo.
- `400 Bad Request`: validación o regla de negocio incumplida.
- `401 Unauthorized`: falta un JWT válido o las credenciales no son correctas.
- `403 Forbidden`: la cuenta no tiene el rol o la asignación requerida.
- `404 Not Found`: no existe el recurso solicitado.
- `409 Conflict`: duplicados o conflicto con registros relacionados.

Los errores manejados por `GlobalExceptionHandler` usan el esquema `ApiError` con `timestamp`, `status`, `error`, `message` y `path`. Los rechazos que ocurren en los filtros de Spring Security pueden tener el formato propio del servidor de seguridad.
