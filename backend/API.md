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

## Catálogo de endpoints

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/api/auth/register` | Público; registra un estudiante |
| `POST` | `/api/auth/login` | Público |
| `GET` | `/api/programas` | Público |
| `GET` | `/api/programas/{idPrograma}` | Público |
| `GET` | `/api/programas/estado/{estadoPrograma}` | Público; `ACTIVO` o `INACTIVO` |
| `POST`, `PUT`, `DELETE` | `/api/programas` y `/api/programas/{idPrograma}` | Administrador |
| `GET` | `/api/clases`, `/api/clases/{idClase}` | Público |
| `POST`, `PUT`, `DELETE` | `/api/clases` y `/api/clases/{idClase}` | Administrador |
| `GET` | `/api/horarios`, `/api/horarios/clase/{idClase}` | Público |
| `POST`, `PUT`, `DELETE` | `/api/horarios` y `/api/horarios/{idHorario}` | Administrador |
| `GET` | `/api/sesiones/clase/{idClase}` | Público |
| `POST` | `/api/sesiones` | Administrador |
| `PATCH` | `/api/sesiones/{idSesion}/estado` | Administrador |
| `GET` | `/api/usuarios/estudiantes`, `/api/usuarios/profesores` | Administrador; acepta `?buscar=` |
| `POST` | `/api/usuarios/profesores` | Administrador |
| `GET` | `/api/inscripciones` | Administrador |
| `GET` | `/api/inscripciones/mias` | Estudiante autenticado |
| `POST` | `/api/inscripciones` | Estudiante autenticado |
| `PATCH` | `/api/inscripciones/{idInscripcion}/estado` | Administrador |
| `GET` | `/api/asistencias/mias` | Estudiante autenticado |
| `GET` | `/api/sesiones/{idSesion}/asistencias` | Administrador o profesor asignado |
| `PUT` | `/api/sesiones/{idSesion}/asistencias/{idInscripcion}` | Profesor asignado |

La lista generada al iniciar la API en `/docs.json` es la referencia ejecutable de los 31 métodos y rutas publicados.

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

### Estados de inscripción

- Una solicitud nueva empieza como `PENDIENTE`.
- Administración puede aprobarla (`ACTIVA`) o cancelarla (`CANCELADA`).
- Una inscripción activa puede cancelarse.
- Una inscripción cancelada puede reabrirse como `PENDIENTE`; la operación vuelve a comprobar el cupo y responde `409 Conflict` si la clase está llena. Después se puede aprobar con el flujo normal.

### Reglas para programar sesiones

`POST /api/sesiones` recibe `idHorario` y `fecha` en formato `AAAA-MM-DD`. La fecha debe caer en el día semanal de ese horario y estar entre `fechaInicio` y `fechaFinalizacion` de la clase, inclusive. Por ejemplo, una clase que comienza el lunes 9 de noviembre no admite una sesión el martes 3 de noviembre, aunque el horario sea de martes; el primer martes dentro del periodo sería el 10 de noviembre.

## Verificaciones realizadas

- `.\mvnw.cmd -B test` desde `backend`: 4 pruebas pasaron (carga del contexto y tres reglas de reapertura de inscripciones).
- `npm run build` desde `frontend`: compilación de producción correcta.
- Barrido de las 31 operaciones publicadas: consultas públicas, validación, rutas inexistentes y llamadas protegidas sin token respondieron con el estado esperado. También se verificaron permisos con roles de prueba locales: accesos administrativos con `200`, rol insuficiente con `403` y validación sin escritura con `400`.
- Flujo CRUD de un programa temporal: creación `201`, consulta `200`, actualización `200`, filtro `200`, eliminación `204` y confirmación posterior `404`. El registro temporal se eliminó al terminar.
- `GET /health`, Swagger UI, especificación `/docs.json` y preflight CORS del origen `http://localhost:5173`: `200`.
- Se corrigió el manejo de parámetros de ruta con tipo/valor incorrecto: ahora devuelven `400` con `ApiError`, en vez de caer en el manejador de `500`.

Las operaciones de escritura con profesor, horarios, sesiones, inscripciones y asistencias no se ejecutaron con datos válidos sobre la base personal. Para la demo, pruébalas con registros preparados y elimina solo datos de demostración que hayas creado.
