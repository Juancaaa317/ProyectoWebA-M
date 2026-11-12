# Contrato propuesto: módulo de pagos y comprobantes

**Estado: PROPUESTA, pendiente de acuerdo con backend.** Nada de esto existe aún en la API.

## Lo que existe hoy

- BD (`BaseSQL/academia_db.sql`):
  - `pago(idPago, idEstudiante, idArchivo NULL, monto decimal(12,2), fechaPago date NULL, estado enum('PENDIENTE','APROBADO','RECHAZADO'))`
  - `archivo(idArchivo, idUsuario, nombre, tamanoBytes, ruta, fechaSubida, tipoMime)`
- Backend Java: no hay entidades, repositorios, servicios ni controladores de `Pago` ni `Archivo`; tampoco hay endpoints `/pagos` ni subida multipart.
- Frontend: menús "Pagos" (admin) y "Mis pagos" (estudiante) en `config/roles.js`; la ruta `/panel/pagos` cae hoy en `SeccionPendiente` ("EN CONSTRUCCIÓN").

## Lo que debe acordar el equipo backend

Los estados `PENDIENTE | APROBADO | RECHAZADO` salen del esquema SQL; los demás nombres son propuestas a confirmar.

### Estudiante (rol ESTUDIANTE; el servidor filtra por el usuario del JWT)

| Operación | Propuesta |
|---|---|
| Listar mis pagos | `GET /api/pagos/mios` |
| Enviar comprobante | `POST /api/pagos` (`multipart/form-data`) |
| Descargar mi comprobante | `GET /api/pagos/{idPago}/archivo` (binario; solo si el pago es mío) |

### Administrador (rol ADMINISTRADOR)

| Operación | Propuesta |
|---|---|
| Listar pagos | `GET /api/pagos` con filtros opcionales `estado`, `desde`, `hasta`, `q` (estudiante) |
| Descargar comprobante | `GET /api/pagos/{idPago}/archivo` |
| Cambiar estado | `PATCH /api/pagos/{idPago}/estado` con `{ "estado": "APROBADO" \| "RECHAZADO" }` |

### Datos a confirmar

1. Campos del formulario multipart (partes y nombres): ¿`monto`, `fechaPago`, `archivo`? ¿Alguno más (concepto, mes, clase)?
2. Respuesta de listado: campos de cada pago (¿`idPago`, `monto`, `fechaPago`, `estado`, nombre/tipo del archivo, y para admin nombre/correo del estudiante?).
3. Límite de tamaño y tipos MIME permitidos (el frontend debe validarlos igual que el servidor).
4. Si se puede reenviar un pago rechazado, y si el rechazo lleva motivo (hoy el esquema no lo guarda).
5. Si la descarga requiere el JWT (lo normal): el frontend la pediría con `fetch` + `Authorization` y abriría un `Blob`, sin poner el token en la URL.
6. Paginación del listado.
7. Códigos de error y formato `{ "message": ... }` (ya usado por `apiFetch`).
8. Seguridad en servidor: validar rol y propiedad en cada endpoint; no confiar en el nombre de archivo del cliente; guardar fuera de la carpeta pública.

## Plan de frontend cuando el contrato esté cerrado

- `src/services/pagosService.js` (usa `apiFetch`).
- `apiFetch`: no fijar `Content-Type` si `body` es `FormData`; agregar un helper para respuestas binarias (`Blob`).
- `src/pages/Pagos.jsx` (estudiante: historial + formulario) y `src/pages/AdminPagos.jsx` (listado, filtros, aprobar/rechazar), con `PrivateRoute` por rol en `App.jsx`.
- Sin dependencias nuevas; archivo y token solo en memoria.
