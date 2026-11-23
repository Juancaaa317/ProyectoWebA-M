# Sistema de Gestión - Academia Arte y Movimiento

Universidad Autónoma de Occidente - Proyecto Final Desarrollo Web Full-Stack

*Planteamiento del Problema*

Tras los eventos sociales ocurridos en Cali durante 2021 y un posterior cambio de ubicación, la academia de danza Arte y Movimiento experimentó una disminución en su visibilidad y captación de alumnos, contando actualmente con una base de 23 estudiantes. Además de la falta de presencia digital para promocionar su historia y logros, la academia enfrenta un reto operativo: la gestión de pagos, asignación de horarios, inscripciones y control de asistencia se realiza de forma manual y mediante herramientas clasicas. Esto dificulta la escalabilidad del negocio y genera desorden administrativo tanto para el personal como para los estudiantes.

*Objetivos Específicos*

Visibilizar la academia: Diseñar un portal público interactivo que exponga la historia, logros y oferta de profesores para incentivar la captación de nuevos estudiantes.

Digitalizar el control operativo: Implementar un sistema de roles que permita a los administradores y profesores gestionar horarios, clases y registros de asistencia.

Centralizar la gestión financiera: Habilitar un módulo seguro para que los estudiantes carguen sus comprobantes de pago y los administradores puedan auditarlos.

*Alcance del Proyecto*

Desarrollar una aplicación web full-stack funcional que centralice y automatice la gestión de la academia A&M. El sistema estará dividido en dos frentes principales:

*Portal Público (Landing Page):* Un espacio web moderno e interactivo que visibilice la historia, profesores y logros de la academia con el objetivo de atraer nuevos estudiantes.

*Plataforma de Gestión Interna:* Un sistema seguro con control de acceso y separación de rutas que administre los horarios de clases, toma de asistencia y gestión financiera. El sistema garantizará la persistencia dual (base de datos y sistema de archivos) para los soportes de pago cargados por los usuarios.

Roles de Usuario

*Administrador:* Posee control total del sistema. Puede crear y modificar horarios, clases, gestionar inscripciones, registrar profesores, auditar pagos y visualizar el registro general de asistencias.

 *Profesor:* Cuenta con acceso a la visualización de sus horarios asignados y es el encargado oficial de registrar o modificar la asistencia de los alumnos en sus respectivas clases.

*Estudiante:* A través de un panel protegido, puede visualizar la oferta de profesores y horarios, inscribirse a clases específicas, consultar su historial de asistencia y cargar digitalmente sus comprobantes de pago de mensualidades.

Arquitectura y Tecnologias

*Frontend:* React + Vite (HTML5, CSS3, JavaScript/JSX) para la interfaz de usuario y consumo de la API REST.

*Backend:* Java + Spring Boot, encargado de implementar el servidor y exponer los servicios de la aplicación mediante una API REST.

*Base de Datos:* MySQL, utilizada para almacenar la información de usuarios, clases, horarios, inscripciones, asistencia y comprobantes de pago.

*Persistencia de Archivos:* Se utilizará MySQL para la persistencia de la información del sistema y almacenamiento local para los archivos asociados.

## Ejecución local

Requisitos: Java 21, MySQL 8 y Node.js con npm.

1. Crea la base `academia_db` e importa el esquema de `BaseSQL/academia_db.sql`.
2. Copia `backend/.env.example` a `backend/.env` y completa las credenciales MySQL y una clave JWT Base64 segura de al menos 32 bytes.
3. En una terminal, desde `backend`, ejecuta `./mvnw spring-boot:run` (Windows: `./mvnw.cmd spring-boot:run`).
4. Copia `frontend/.env.example` a `frontend/.env`; ajusta `VITE_API_URL` solo si el backend usa otra dirección.
5. En otra terminal, desde `frontend`, ejecuta `npm install` y `npm run dev`.

No subas los archivos `.env` reales ni credenciales al repositorio. Para ver y probar la API con autenticación abre [Swagger UI](http://localhost:8080/docs); la [guía completa](backend/API.md) incluye los roles, rutas y payloads. El módulo de pagos y archivos se describe en la sección «Pagos y archivos» de esa guía.

### Datos de demostración

El script `BaseSQL/academia_db.sql` ya incluye datos de ejemplo (usuarios, programas, clases, horarios, inscripciones, una sesión y una asistencia).

- **Administrador de demostración:** `admin.prueba@ejemplo.com` con la contraseña `ClavePrueba123`. Solo para entornos locales; cámbiala desde `/panel/perfil` si el sistema se publica.
- **Estudiante:** crea una cuenta en `/register`. Los campos de tarjeta de identidad y fecha de nacimiento son opcionales.
- **Profesor:** lo registra el administrador en `/panel/profesores`.
- **Otro administrador:** el registro público siempre crea estudiantes. Para promover una cuenta ejecuta en MySQL: `UPDATE usuario SET rol = 'ADMINISTRADOR' WHERE correo = 'tu@correo.com';`

### Archivos subidos (comprobantes de pago)

Los comprobantes se guardan con un nombre UUID en la carpeta definida por `STORAGE_PATH` (opcional en `backend/.env`); por defecto es `./uploads`, relativa a **la carpeta desde la que se arranca el backend**. Sus metadatos (nombre original, tipo, tamaño, autor, fecha y ruta) quedan en la tabla `archivo` de MySQL. Arranca siempre el backend desde la misma carpeta, o fija `STORAGE_PATH` con una ruta absoluta, para que los archivos ya subidos sigan encontrándose después de reiniciar. La carpeta se crea sola al subir el primer archivo y está excluida de Git.

### Limitaciones conocidas

- **Sin límite de intentos de inicio de sesión:** BCrypt frena la fuerza bruta pero no la impide. En producción conviene limitarlo en el proxy inverso o con un filtro de tasa.
- **Sin revocación de tokens JWT:** un token sigue válido hasta vencer (60 minutos), aunque se cambie la contraseña o se cierre la sesión; el cierre de sesión solo borra el token del navegador.
- **Almacenamiento local de archivos:** en servicios con disco efímero los comprobantes se pierden en cada despliegue; habría que migrar a un almacenamiento externo (S3, Supabase Storage o Cloudinary).
- **La landing pública usa datos de ejemplo** para la oferta de clases, el horario y los profesores; el panel privado sí consume la API real.

## Verificación antes de entregar

- Backend, desde `backend`: `./mvnw test` (Windows: `./mvnw.cmd test`).
- Frontend, desde `frontend`: `npm run lint` y `npm run build`.
- Ejecuta además el recorrido integrado de administrador, estudiante y profesor descrito en la guía de verificación.
