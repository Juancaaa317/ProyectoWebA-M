# Sistema de Gestión - Academia Arte y Movimiento
**Universidad Autónoma de Occidente - Proyecto Final Desarrollo Web Full-Stack**

## 1. Contexto y Planteamiento del Problema
Tras los eventos sociales ocurridos en Cali durante 2021 y un posterior cambio de ubicación, la academia de baile Arte y Movimiento experimentó una disminución en su visibilidad y captación de alumnos, contando actualmente con una base de 26 estudiantes. Además de la falta de presencia digital para promocionar su historia y logros, la academia enfrenta un reto operativo: la gestión de pagos, asignación de horarios, inscripciones y control de asistencia se realiza de forma manual y desarticulada mediante herramientas descentralizadas. Esto dificulta la escalabilidad del negocio y genera desorden administrativo tanto para el personal como para los estudiantes.

## 2. Alcance del Proyecto
Desarrollar una aplicación web full-stack funcional que centralice y automatice la gestión de la academia. El sistema estará dividido en dos frentes principales:
* **Portal Público (Landing Page):** Un espacio web moderno e interactivo que visibilice la historia, instructores y logros de la academia con el objetivo de atraer nuevos estudiantes.
* **Plataforma de Gestión Interna:** Un sistema seguro con control de acceso y separación de rutas que administre la oferta de clases, toma de asistencia y gestión financiera. El sistema garantizará la persistencia dual (base de datos y sistema de archivos) para los soportes de pago cargados por los usuarios.

## 3. Roles de Usuario
* **Administrador:** Posee control total del sistema. Puede crear y modificar horarios, clases, gestionar inscripciones, registrar profesores, auditar pagos y visualizar el registro general de asistencias.
* **Profesor:** Cuenta con acceso a la visualización de sus horarios asignados y es el encargado oficial de registrar o modificar la asistencia de los alumnos en sus respectivas clases.
* **Estudiante:** A través de un panel protegido, puede visualizar la oferta de profesores y horarios, inscribirse a clases específicas, consultar su historial de asistencia y cargar digitalmente sus comprobantes de pago de mensualidades.

## 4. Arquitectura y Tecnologías
* **Frontend:** React + Vite (HTML5, CSS3, JavaScript/JSX) para la interfaz de usuario y consumo de la API REST.
* **Backend:** Lenguaje y framework por definir (se implementará un servidor que exponga endpoints REST semánticos).
* **Base de Datos:** Motor por definir (SQL o NoSQL) para estructurar usuarios, clases, horarios e inscripciones.
* **Persistencia de Archivos:** Sistema por definir (almacenamiento local o en la nube) para guardar los comprobantes de pago físico y enlazar sus metadatos en la base de datos.