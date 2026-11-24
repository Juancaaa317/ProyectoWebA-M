-- ============================================================
-- Academia Arte y Movimiento: estructura de la base y datos de demostración
-- ============================================================
-- Uso (MySQL 8):
--   mysql -u root -p < BaseSQL/academia_db.sql
-- o abrirlo en MySQL Workbench y ejecutarlo completo.
--
-- ATENCIÓN: borra y vuelve a crear todas las tablas de `academia_db`.
-- Si ya tienes datos que quieras conservar, haz antes un respaldo:
--   mysqldump -u root -p academia_db > respaldo.sql
--
-- Cuentas de demostración (todas con la contraseña ClavePrueba123):
--   Administrador: admin.prueba@ejemplo.com
--   Profesora:     profesor.prueba@ejemplo.com    (Laura Gómez: Ballet infantil y Danza árabe)
--   Profesor:      profesor2.prueba@ejemplo.com   (Andrés Restrepo: Danza acrobática y Contemporánea)
--   Estudiante:    estudiante.prueba@ejemplo.com  (Sofía Martínez: inscripciones y asistencia)
--   Estudiante:    estudiante2.prueba@ejemplo.com (Valentina Ríos)
--   Estudiante:    estudiante3.prueba@ejemplo.com (Camila Torres: inscripciones pendientes)
-- Solo para entornos locales: cambia las contraseñas si publicas el sistema.
--
-- No incluye pagos: cada pago necesita un comprobante real en disco. Para probar
-- ese flujo, entra como estudiante y sube uno desde "Mis pagos".
-- ============================================================

CREATE DATABASE IF NOT EXISTS `academia_db`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `academia_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- Estructura
-- ------------------------------------------------------------

DROP TABLE IF EXISTS `acudiente`;
CREATE TABLE `acudiente` (
  `idAcudiente` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `direccionRes` varchar(255) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `cedulaCiudadana` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`idAcudiente`),
  UNIQUE KEY `cedulaCiudadana` (`cedulaCiudadana`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `archivo`;
CREATE TABLE `archivo` (
  `idArchivo` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `nombreOriginal` varchar(255) NOT NULL,
  `tamanoBytes` bigint NOT NULL,
  `ruta` varchar(500) NOT NULL,
  `fechaSubida` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipoMime` varchar(150) NOT NULL,
  PRIMARY KEY (`idArchivo`),
  UNIQUE KEY `uq_archivo_ruta` (`ruta`),
  KEY `fk_archivo_usuario` (`idUsuario`),
  CONSTRAINT `fk_archivo_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `asistencia`;
CREATE TABLE `asistencia` (
  `idAsistencia` int NOT NULL AUTO_INCREMENT,
  `idInscripcion` int NOT NULL,
  `idSesion` int NOT NULL,
  `estado` enum('PRESENTE','AUSENTE','JUSTIFICADO') NOT NULL,
  `observacion` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`idAsistencia`),
  UNIQUE KEY `uq_asistencia_inscripcion_sesion` (`idInscripcion`,`idSesion`),
  KEY `fk_asistencia_sesion` (`idSesion`),
  CONSTRAINT `fk_asistencia_inscripcion` FOREIGN KEY (`idInscripcion`) REFERENCES `inscripcion` (`idInscripcion`),
  CONSTRAINT `fk_asistencia_sesion` FOREIGN KEY (`idSesion`) REFERENCES `sesion_clase` (`idSesion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `clase`;
CREATE TABLE `clase` (
  `idClase` int NOT NULL AUTO_INCREMENT,
  `idProfesor` int NOT NULL,
  `idPrograma` int NOT NULL,
  `fechaInicio` date DEFAULT NULL,
  `fechaFinalizacion` date DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `cupoMaximo` int NOT NULL,
  `estadoClase` enum('PROGRAMADA','EN_CURSO','FINALIZADA','CANCELADA') NOT NULL,
  PRIMARY KEY (`idClase`),
  KEY `fk_clase_profesor` (`idProfesor`),
  KEY `fk_clase_programa` (`idPrograma`),
  CONSTRAINT `fk_clase_profesor` FOREIGN KEY (`idProfesor`) REFERENCES `profesor` (`idProfesor`),
  CONSTRAINT `fk_clase_programa` FOREIGN KEY (`idPrograma`) REFERENCES `programa` (`idPrograma`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `estudiante`;
CREATE TABLE `estudiante` (
  `idEstudiante` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `tarjetaIdentidad` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`idEstudiante`),
  UNIQUE KEY `idUsuario` (`idUsuario`),
  UNIQUE KEY `tarjetaIdentidad` (`tarjetaIdentidad`),
  CONSTRAINT `fk_estudiante_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `estudiante_acudiente`;
CREATE TABLE `estudiante_acudiente` (
  `idEstudiante` int NOT NULL,
  `idAcudiente` int NOT NULL,
  `parentesco` varchar(80) DEFAULT NULL,
  `contactoPrincipal` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idEstudiante`,`idAcudiente`),
  KEY `fk_estudiante_acudiente_acudiente` (`idAcudiente`),
  CONSTRAINT `fk_estudiante_acudiente_acudiente` FOREIGN KEY (`idAcudiente`) REFERENCES `acudiente` (`idAcudiente`),
  CONSTRAINT `fk_estudiante_acudiente_estudiante` FOREIGN KEY (`idEstudiante`) REFERENCES `estudiante` (`idEstudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `horario`;
CREATE TABLE `horario` (
  `idHorario` int NOT NULL AUTO_INCREMENT,
  `idClase` int NOT NULL,
  `diaSemana` enum('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL,
  `horaInicio` time NOT NULL,
  `horaFin` time NOT NULL,
  PRIMARY KEY (`idHorario`),
  KEY `fk_horario_clase` (`idClase`),
  CONSTRAINT `fk_horario_clase` FOREIGN KEY (`idClase`) REFERENCES `clase` (`idClase`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `inscripcion`;
CREATE TABLE `inscripcion` (
  `idInscripcion` int NOT NULL AUTO_INCREMENT,
  `idEstudiante` int NOT NULL,
  `idClase` int NOT NULL,
  `fechaInscripcion` date NOT NULL,
  `estado` enum('PENDIENTE','ACTIVA','CANCELADA') NOT NULL,
  PRIMARY KEY (`idInscripcion`),
  UNIQUE KEY `uq_inscripcion_estudiante_clase` (`idEstudiante`,`idClase`),
  KEY `fk_inscripcion_clase` (`idClase`),
  CONSTRAINT `fk_inscripcion_clase` FOREIGN KEY (`idClase`) REFERENCES `clase` (`idClase`),
  CONSTRAINT `fk_inscripcion_estudiante` FOREIGN KEY (`idEstudiante`) REFERENCES `estudiante` (`idEstudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `pago`;
CREATE TABLE `pago` (
  `idPago` int NOT NULL AUTO_INCREMENT,
  `idEstudiante` int NOT NULL,
  `idArchivo` int DEFAULT NULL,
  `monto` decimal(12,2) NOT NULL,
  `periodo` char(7) NOT NULL,
  `fechaPago` date DEFAULT NULL,
  `estado` enum('PENDIENTE','APROBADO','RECHAZADO') NOT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idPago`),
  KEY `fk_pago_estudiante` (`idEstudiante`),
  KEY `fk_pago_archivo` (`idArchivo`),
  KEY `idx_pago_estudiante_periodo` (`idEstudiante`,`periodo`),
  CONSTRAINT `fk_pago_archivo` FOREIGN KEY (`idArchivo`) REFERENCES `archivo` (`idArchivo`),
  CONSTRAINT `fk_pago_estudiante` FOREIGN KEY (`idEstudiante`) REFERENCES `estudiante` (`idEstudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `profesor`;
CREATE TABLE `profesor` (
  `idProfesor` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `direccionRes` varchar(255) DEFAULT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `cedulaCiudadana` varchar(30) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `areaDocencia` enum('DOCENTE','PROFESIONAL','ESPECIALIZADO') DEFAULT NULL,
  `observaciones` text,
  PRIMARY KEY (`idProfesor`),
  UNIQUE KEY `idUsuario` (`idUsuario`),
  UNIQUE KEY `cedulaCiudadana` (`cedulaCiudadana`),
  CONSTRAINT `fk_profesor_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `programa`;
CREATE TABLE `programa` (
  `idPrograma` int NOT NULL AUTO_INCREMENT,
  `nombrePrograma` varchar(100) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `estadoPrograma` enum('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`idPrograma`),
  UNIQUE KEY `nombrePrograma` (`nombrePrograma`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `sesion_clase`;
CREATE TABLE `sesion_clase` (
  `idSesion` int NOT NULL AUTO_INCREMENT,
  `idClase` int NOT NULL,
  `fecha` date NOT NULL,
  `horaInicio` time NOT NULL,
  `horaFin` time NOT NULL,
  `estado` enum('PROGRAMADA','REALIZADA','CANCELADA') NOT NULL,
  PRIMARY KEY (`idSesion`),
  UNIQUE KEY `uq_sesion_clase_fecha_hora` (`idClase`,`fecha`,`horaInicio`),
  KEY `fk_sesion_clase_clase` (`idClase`),
  CONSTRAINT `fk_sesion_clase_clase` FOREIGN KEY (`idClase`) REFERENCES `clase` (`idClase`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `correo` varchar(150) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `rol` enum('ADMINISTRADOR','PROFESOR','ESTUDIANTE') NOT NULL,
  `nombre` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------
-- Datos de demostración
-- ------------------------------------------------------------

-- Usuarios (contrasena = BCrypt de "ClavePrueba123")
INSERT INTO `usuario` (`idUsuario`, `correo`, `telefono`, `contrasena`, `rol`, `nombre`) VALUES
  (1, 'admin.prueba@ejemplo.com',       '3000000001', '$2a$10$H6U2iPxaO3ktzNxNC2VLx.7ddMS6VwC84PCnsEs4gb7LqH6El3vNS', 'ADMINISTRADOR', 'Administración A&M'),
  (2, 'profesor.prueba@ejemplo.com',    '3000000002', '$2a$10$H6U2iPxaO3ktzNxNC2VLx.7ddMS6VwC84PCnsEs4gb7LqH6El3vNS', 'PROFESOR',      'Laura Gómez'),
  (3, 'profesor2.prueba@ejemplo.com',   '3000000003', '$2a$10$H6U2iPxaO3ktzNxNC2VLx.7ddMS6VwC84PCnsEs4gb7LqH6El3vNS', 'PROFESOR',      'Andrés Restrepo'),
  (4, 'estudiante.prueba@ejemplo.com',  '3000000004', '$2a$10$H6U2iPxaO3ktzNxNC2VLx.7ddMS6VwC84PCnsEs4gb7LqH6El3vNS', 'ESTUDIANTE',    'Sofía Martínez'),
  (5, 'estudiante2.prueba@ejemplo.com', '3000000005', '$2a$10$H6U2iPxaO3ktzNxNC2VLx.7ddMS6VwC84PCnsEs4gb7LqH6El3vNS', 'ESTUDIANTE',    'Valentina Ríos'),
  (6, 'estudiante3.prueba@ejemplo.com', '3000000006', '$2a$10$H6U2iPxaO3ktzNxNC2VLx.7ddMS6VwC84PCnsEs4gb7LqH6El3vNS', 'ESTUDIANTE',    'Camila Torres');

INSERT INTO `profesor` (`idProfesor`, `idUsuario`, `nombre`, `direccionRes`, `fechaNacimiento`, `cedulaCiudadana`, `telefono`, `areaDocencia`, `observaciones`) VALUES
  (1, 2, 'Laura Gómez',     NULL, NULL, NULL, '3000000002', 'PROFESIONAL',   NULL),
  (2, 3, 'Andrés Restrepo', NULL, NULL, NULL, '3000000003', 'ESPECIALIZADO', NULL);

INSERT INTO `estudiante` (`idEstudiante`, `idUsuario`, `nombre`, `fechaNacimiento`, `tarjetaIdentidad`) VALUES
  (1, 4, 'Sofía Martínez', '2012-03-14', '1000000001'),
  (2, 5, 'Valentina Ríos', '2010-08-02', '1000000002'),
  (3, 6, 'Camila Torres',  '2008-11-20', NULL);

INSERT INTO `acudiente` (`idAcudiente`, `nombre`, `direccionRes`, `telefono`, `cedulaCiudadana`) VALUES
  (1, 'María Martínez', 'Cali, Valle del Cauca', '3000000011', '1000000011');

INSERT INTO `estudiante_acudiente` (`idEstudiante`, `idAcudiente`, `parentesco`, `contactoPrincipal`) VALUES
  (1, 1, 'Madre', 1);

INSERT INTO `programa` (`idPrograma`, `nombrePrograma`, `descripcion`, `estadoPrograma`) VALUES
  (1, 'Ballet',           'Técnica clásica, postura y elegancia para todas las edades.',           'ACTIVO'),
  (2, 'Danza acrobática', 'Flexibilidad, fuerza y acrobacia combinadas con danza.',                'ACTIVO'),
  (3, 'Danza árabe',      'Ritmo, expresión y movimiento fluido.',                                 'ACTIVO'),
  (4, 'Contemporánea',    'Exploración del movimiento, improvisación y composición coreográfica.', 'ACTIVO'),
  (5, 'Ritmos latinos',   'Salsa, bachata y merengue. Programa en pausa este periodo.',            'INACTIVO');

-- Clases del periodo 2026-2027. Los horarios de cada profesor no se cruzan.
INSERT INTO `clase` (`idClase`, `idProfesor`, `idPrograma`, `fechaInicio`, `fechaFinalizacion`, `nombre`, `cupoMaximo`, `estadoClase`) VALUES
  (1, 1, 1, '2026-11-02', '2027-02-26', 'Ballet infantil',          15, 'EN_CURSO'),
  (2, 2, 2, '2026-11-02', '2027-02-26', 'Danza acrobática juvenil', 12, 'EN_CURSO'),
  (3, 1, 3, '2026-11-02', '2027-02-26', 'Danza árabe',              10, 'EN_CURSO'),
  (4, 2, 4, '2027-01-11', '2027-04-30', 'Contemporánea adultos',    12, 'PROGRAMADA');

INSERT INTO `horario` (`idHorario`, `idClase`, `diaSemana`, `horaInicio`, `horaFin`) VALUES
  (1, 1, 'LUNES',     '16:00:00', '17:30:00'),
  (2, 1, 'MIERCOLES', '16:00:00', '17:30:00'),
  (3, 1, 'SABADO',    '09:00:00', '10:30:00'),
  (4, 2, 'LUNES',     '17:30:00', '19:00:00'),
  (5, 2, 'MIERCOLES', '17:30:00', '19:00:00'),
  (6, 2, 'SABADO',    '11:00:00', '12:30:00'),
  (7, 3, 'MARTES',    '16:00:00', '17:30:00'),
  (8, 3, 'JUEVES',    '16:00:00', '17:30:00'),
  (9, 4, 'VIERNES',   '18:00:00', '19:30:00');

INSERT INTO `inscripcion` (`idInscripcion`, `idEstudiante`, `idClase`, `fechaInscripcion`, `estado`) VALUES
  (1, 1, 1, '2026-10-28', 'ACTIVA'),
  (2, 1, 3, '2026-10-28', 'ACTIVA'),
  (3, 2, 1, '2026-10-29', 'ACTIVA'),
  (4, 2, 2, '2026-10-29', 'ACTIVA'),
  (5, 3, 2, '2026-11-20', 'PENDIENTE'),
  (6, 3, 4, '2026-11-20', 'PENDIENTE');

-- Sesiones ya dictadas (con asistencia) y una próxima. Cada fecha cae en un día de su horario.
INSERT INTO `sesion_clase` (`idSesion`, `idClase`, `fecha`, `horaInicio`, `horaFin`, `estado`) VALUES
  (1, 1, '2026-11-16', '16:00:00', '17:30:00', 'REALIZADA'),
  (2, 1, '2026-11-18', '16:00:00', '17:30:00', 'REALIZADA'),
  (3, 2, '2026-11-16', '17:30:00', '19:00:00', 'REALIZADA'),
  (4, 3, '2026-11-17', '16:00:00', '17:30:00', 'REALIZADA'),
  (5, 1, '2026-11-30', '16:00:00', '17:30:00', 'PROGRAMADA');

INSERT INTO `asistencia` (`idAsistencia`, `idInscripcion`, `idSesion`, `estado`, `observacion`) VALUES
  (1, 1, 1, 'PRESENTE',    NULL),
  (2, 3, 1, 'AUSENTE',     NULL),
  (3, 1, 2, 'PRESENTE',    NULL),
  (4, 3, 2, 'JUSTIFICADO', 'Cita médica'),
  (5, 4, 3, 'PRESENTE',    NULL),
  (6, 2, 4, 'PRESENTE',    NULL);

SET FOREIGN_KEY_CHECKS = 1;
