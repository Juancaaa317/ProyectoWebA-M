# Guía de la API REST

La API del sistema está implementada con Spring Boot y documentada con OpenAPI. Swagger UI se genera desde los controladores y DTO actuales; úsala como referencia interactiva cuando el backend esté iniciado.

## Inicio local

1. Instala Java 21 y MySQL 8.
2. Crea la base de datos y sus tablas con el script de `BaseSQL/` si aún no existen.
3. Copia `backend/.env.example` a `backend/.env` y configura `DB_USERNAME`, `DB_PASSWORD` y `APP_JWT_SECRET`. El secreto JWT debe ser una clave aleatoria Base64 de al menos 32 bytes. No publiques `backend/.env`.
4. Desde `backend`, inicia la API:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. Abre Swagger UI en <http://localhost:8080/docs>. El documento OpenAPI JSON está en <http://localhost:8080/docs.json>.

El frontend configura la dirección de la API desde `frontend/.env`, tomando `frontend/.env.example` como referencia. Para el entorno local, `VITE_API_URL` suele ser `http://localhost:8080/api`.

## Autenticación en Swagger UI

1. Ejecuta `POST /api/auth/login` con el correo y la contraseña de una cuenta existente.
2. Copia el campo `token` de la respuesta.
3. Pulsa **Authorize** y pega el token sin el prefijo `Bearer`.
4. Ejecuta las rutas protegidas permitidas para el rol de esa cuenta.

Swagger no eleva permisos. El backend verifica el JWT y el rol en cada operación protegida. Los archivos se consultan enviando el JWT en la cabecera `Authorization`, nunca como parámetro de URL.

## Rutas y permisos

### Autenticación

| Método | Ruta | Acceso | Resultado habitual |
|---|---|---|---|
| `POST` | `/api/auth/register` | Público | `201`; crea una cuenta de estudiante |
| `POST` | `/api/auth/login` | Público | `200`; devuelve JWT y datos básicos de sesión |

### Programas, clases y horarios

| Método | Ruta | Acceso |
|---|---|---|
| `GET` | `/api/programas` | Público |
| `GET` | `/api/programas/{idPrograma}` | Público |
| `GET` | `/api/programas/estado/{estadoPrograma}` | Público; `ACTIVO` o `INACTIVO` |
| `POST`, `PUT`, `DELETE` | `/api/programas`, `/api/programas/{idPrograma}` | Administrador |
| `GET` | `/api/clases`, `/api/clases/{idClase}` | Público |
| `GET` | `/api/clases/mias` (clases asignadas al profesor del token) | Profesor |
| `POST`, `PUT`, `DELETE` | `/api/clases`, `/api/clases/{idClase}` | Administrador |
| `GET` | `/api/horarios`, `/api/horarios/clase/{idClase}` | Público |
| `POST`, `PUT`, `DELETE` | `/api/horarios`, `/api/horarios/{idHorario}` | Administrador |
| `GET` | `/api/sesiones/clase/{idClase}` | Público |
| `POST` | `/api/sesiones` | Administrador |
| `PATCH` | `/api/sesiones/{idSesion}/estado` | Administrador |

### Usuarios, inscripciones y asistencia

| Método | Ruta | Acceso |
|---|---|---|
| `GET` | `/api/usuarios/me` | Cualquier usuario autenticado; consulta su perfil |
| `PUT` | `/api/usuarios/me` | Cualquier usuario autenticado; actualiza nombre y teléfono |
| `PUT` | `/api/usuarios/me/contrasena` | Cualquier usuario autenticado; cambia su contraseña verificando la actual |
| `GET` | `/api/usuarios/estudiantes`, `/api/usuarios/profesores` | Administrador; acepta `?buscar=` |
| `POST` | `/api/usuarios/profesores` | Administrador |
| `POST` | `/api/inscripciones` | Estudiante; solicita inscripción en una clase por `idClase` |
| `GET` | `/api/inscripciones/mias` | Estudiante; consulta sus inscripciones |
| `GET` | `/api/inscripciones` | Administrador; consulta todas |
| `PATCH` | `/api/inscripciones/{idInscripcion}/estado` | Administrador; revisa la solicitud |
| `GET` | `/api/asistencias/mias` | Estudiante; consulta su historial |
| `GET` | `/api/sesiones/{idSesion}/asistencias` | Administrador o profesor asignado a la clase |
| `PUT` | `/api/sesiones/{idSesion}/asistencias/{idInscripcion}` | Profesor asignado; registra o actualiza asistencia |

### Pagos y archivos

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/api/pagos` | Estudiante; registra un pago y adjunta el comprobante |
| `GET` | `/api/pagos/mios` | Estudiante; consulta únicamente sus pagos |
| `GET` | `/api/pagos` | Administrador; filtros opcionales `estado` y `periodo` |
| `PATCH` | `/api/pagos/{idPago}/estado` | Administrador; aprueba o rechaza un pago pendiente |
| `GET` | `/api/archivos/{id}` | Dueño del archivo o administrador; `?descargar=true` fuerza descarga |

La autorización se verifica en el servidor además de las rutas protegidas del frontend. Para descargar o ver un archivo, el frontend solicita el recurso con el JWT y abre la respuesta binaria; la URL no contiene el token.

## Payloads de referencia

### Registrar estudiante

`POST /api/auth/register` — `Content-Type: application/json`

```json
{
  "nombre": "Estudiante de Prueba",
  "tarjetaIdentidad": "1234567890",
  "fechaNacimiento": "2005-06-15",
  "correo": "estudiante@example.com",
  "telefono": "3001234567",
  "contrasena": "ClavePrueba123",
  "confirmarContrasena": "ClavePrueba123"
}
```

El rol público de registro es siempre `ESTUDIANTE`; el cliente no puede elegir un rol privilegiado.

### Iniciar sesión

`POST /api/auth/login` — `Content-Type: application/json`

```json
{
  "correo": "estudiante@example.com",
  "password": "ClavePrueba123"
}
```

### Inscribirse a una clase

`POST /api/inscripciones` — requiere token de estudiante.

```json
{
  "idClase": 1
}
```

### Programar una sesión

`POST /api/sesiones` — requiere token de administrador. `idHorario` debe pertenecer a la clase y la fecha debe coincidir con el día semanal del horario y caer dentro de las fechas de la clase.

```json
{
  "idHorario": 1,
  "fecha": "2026-11-10"
}
```

### Registrar asistencia

`PUT /api/sesiones/{idSesion}/asistencias/{idInscripcion}` — requiere token del profesor asignado.

```json
{
  "estado": "PRESENTE",
  "observaciones": ""
}
```

Los valores permitidos para `estado` son `PRESENTE`, `AUSENTE` y `JUSTIFICADO`.

### Crear pago y adjuntar comprobante

`POST /api/pagos` usa `multipart/form-data`, no JSON. Incluye los campos `monto` (mayor que cero), `archivo` (PDF, PNG o JPG de hasta 5 MB) y `periodo` opcional con formato `AAAA-MM`. Si se omite `periodo`, el servidor usa el mes actual. El estado inicial siempre lo asigna el servidor como `PENDIENTE`.

Para rechazar un pago, `PATCH /api/pagos/{idPago}/estado` requiere una observación:

```json
{
  "estado": "RECHAZADO",
  "observacion": "El comprobante no permite verificar el valor pagado."
}
```

Para aprobarlo, envía `{"estado":"APROBADO"}`. Solo se resuelven pagos pendientes.

## Validaciones y respuestas

Los DTO aplican restricciones de campos obligatorios, formato, longitud, rangos, estados permitidos y confirmación de contraseñas. Las reglas que dependen de relaciones o del estado actual se validan en servicios: por ejemplo, capacidad de clase, duplicidad de inscripción, profesor asignado, horario de una sesión y transición de estado de pagos.

| Código | Significado |
|---|---|
| `200 OK` | Consulta o actualización completada |
| `201 Created` | Recurso creado |
| `204 No Content` | Operación completada sin cuerpo, como eliminar o cambiar contraseña |
| `400 Bad Request` | Payload, parámetro o regla de negocio inválidos |
| `401 Unauthorized` | Token ausente o inválido o credenciales incorrectas |
| `403 Forbidden` | Rol o propiedad insuficientes |
| `404 Not Found` | Ruta o recurso inexistente |
| `409 Conflict` | Duplicidad, cupo agotado o conflicto con registros asociados |
| `413 Content Too Large` | Archivo que excede el límite |
| `415 Unsupported Media Type` | Formato de archivo no permitido |

Los errores de la API usan `ApiErrorDTO` con `timestamp`, `status`, `error`, `message` y `path`, incluidos los rechazos `401` y `403` de Spring Security.

## Persistencia de comprobantes

Los bytes del archivo se almacenan en la carpeta local configurada con `STORAGE_PATH` (por defecto `./uploads`) y los metadatos se guardan en MySQL. La ruta relativa se interpreta desde el directorio de trabajo del proceso: al iniciar Maven desde `backend`, queda en `backend/uploads`; al iniciarlo desde la raíz, queda en `uploads`. Mantén el mismo directorio de inicio, o configura `STORAGE_PATH` con una ruta absoluta local, para que la API encuentre los comprobantes después de reiniciar. El nombre físico usa UUID; el servidor valida tamaño, extensión y firma del contenido, y verifica que la ruta resuelta permanezca dentro de la carpeta permitida. Si la transacción de base de datos falla después de guardar el archivo, se elimina el archivo huérfano.

Este modo satisface almacenamiento dual en el entorno local. En un despliegue con disco efímero hará falta almacenamiento persistente externo o un volumen persistente.

## Verificación local

Comandos disponibles:

```powershell
# Desde backend
.\mvnw.cmd test

# Desde frontend
npm run lint
npm run build
```

La suite backend actual contiene pruebas para inscripción, pagos, archivos, almacenamiento local y controladores de pagos/archivos. Para comprobar el flujo integrado, usa cuentas de prueba de cada rol y sigue este orden: administrador crea programa/clase/horario/sesión; estudiante solicita inscripción; administrador la aprueba; profesor asignado registra asistencia; estudiante consulta inscripción e historial. Para el flujo financiero: estudiante envía comprobante; administrador lo revisa y aprueba o rechaza; estudiante consulta estado y observación y abre o descarga el comprobante.

No se fija aquí un conteo de operaciones probadas ni un resultado de ejecución: esos datos deben anotarse después de correr los comandos y el recorrido en el entorno que se va a entregar.
