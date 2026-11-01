## Cómo probar el manejo de errores (Avance 2)

### 1. Ejecutar el frontend

```bash
cd frontend
npm install
npm run dev
```

Abrir en el navegador la dirección que muestre la terminal (normalmente `http://localhost:5173`).

> El archivo `frontend/.env` (plantilla en `.env.example`) tiene `VITE_USE_MOCK=true`, que usa datos simulados.
> Con `VITE_USE_MOCK=false` se usa el backend real.
> Usuario de prueba (solo modo mock): `admin@arteymovimiento.com` / `admin123`

### 2. Pruebas paso a paso

**Prueba 1: Credenciales incorrectas**
1. Ir a **Ingresar**.
2. Escribir correo `x@x.com` y contraseña `123`.
3. Resultado: aparece el mensaje rojo "Correo o contraseña incorrectos".

**Prueba 2: Ruta privada**
1. Sin haber iniciado sesión, escribir en la barra `/admin/programas`.
2. Resultado: el sistema redirige automáticamente al login.

**Prueba 3: Sesión persistente**
1. Iniciar sesión con el usuario de prueba.
2. Presionar F5 en `/admin/programas`.
3. Resultado: la sesión se mantiene y no pide login otra vez.

**Prueba 4: Página 404**
1. Escribir en la barra una dirección inexistente, por ejemplo `/loquesea`.
2. Resultado: se muestra la página "404 – Página no encontrada" con el botón "Volver al inicio".

**Prueba 5: Estado de carga**
1. Entrar a `/admin/programas`.
2. Resultado: se ve un spinner con "Cargando programas..." y luego la tabla.

**Prueba 6: Validación (400)**
1. Pulsar **+ Nuevo programa**.
2. Dejar el nombre vacío y pulsar **Crear programa**.
3. Resultado: "El nombre es obligatorio."

**Prueba 7: Nombre duplicado (409)**
1. Pulsar **+ Nuevo programa**.
2. Escribir el nombre `Ballet` y guardar.
3. Resultado: "Ya existe un programa con ese nombre".

**Prueba 8: CRUD correcto**
1. Crear un programa nuevo: aparece el mensaje verde "Programa creado."
2. Editarlo: aparece "Programa actualizado."
3. Eliminarlo (con confirmación): aparece "Programa eliminado."

**Prueba 9: Error de conexión**
1. En `frontend/src/services/programaService.js`, dentro de `mock.listar`, agregar al inicio:
   `throw new ApiError("No se pudo conectar con el servidor", 0);`
2. Recargar `/admin/programas`.
3. Resultado: mensaje rojo con el botón **Reintentar**.
4. Quitar la línea agregada al terminar.

### 3. Con el backend real

Cambiar `VITE_USE_MOCK=false`, reiniciar `npm run dev` y repetir las pruebas.
En F12 → pestaña **Red** se verifican los códigos: 201 al crear, 204 al eliminar, 400 por datos inválidos, 404 si no existe y 409 si el nombre está duplicado.