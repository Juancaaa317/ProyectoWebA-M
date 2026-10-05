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
