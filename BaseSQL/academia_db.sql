-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: academia_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


create database academia_db;
use academia_db;
--
-- Table structure for table `acudiente`
--

DROP TABLE IF EXISTS `acudiente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acudiente` (
  `idAcudiente` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `direccionRes` varchar(255) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `cedulaCiudadana` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`idAcudiente`),
  UNIQUE KEY `cedulaCiudadana` (`cedulaCiudadana`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acudiente`
--

LOCK TABLES `acudiente` WRITE;
/*!40000 ALTER TABLE `acudiente` DISABLE KEYS */;
/*!40000 ALTER TABLE `acudiente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archivo`
--

DROP TABLE IF EXISTS `archivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archivo` (
  `idArchivo` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `tamanoBytes` bigint NOT NULL,
  `ruta` varchar(500) NOT NULL,
  `fechaSubida` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipoMime` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`idArchivo`),
  KEY `fk_archivo_usuario` (`idUsuario`),
  CONSTRAINT `fk_archivo_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archivo`
--

LOCK TABLES `archivo` WRITE;
/*!40000 ALTER TABLE `archivo` DISABLE KEYS */;
/*!40000 ALTER TABLE `archivo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
INSERT INTO `asistencia` VALUES (1,1,2,'PRESENTE',NULL);
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clase`
--

DROP TABLE IF EXISTS `clase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clase`
--

LOCK TABLES `clase` WRITE;
/*!40000 ALTER TABLE `clase` DISABLE KEYS */;
INSERT INTO `clase` VALUES (1,1,2,'2026-11-09','2027-01-31','Ballet principiantes',15,'PROGRAMADA'),(2,3,11,'2026-11-03','2026-11-05','Danza contemporanea principiantes',10,'PROGRAMADA');
/*!40000 ALTER TABLE `clase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estudiante`
--

DROP TABLE IF EXISTS `estudiante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estudiante`
--

LOCK TABLES `estudiante` WRITE;
/*!40000 ALTER TABLE `estudiante` DISABLE KEYS */;
INSERT INTO `estudiante` VALUES (1,1,'Estudiante Prueba','2005-06-15','1098765432'),(2,2,'Estudiante de Prueba','2005-06-15','1098765433'),(3,3,'Carlos Ramirez','2005-08-20','1098765434'),(4,4,'Carlos Ramirez','2005-08-20','10987654357'),(5,5,'Carlos Ramirez','2005-08-20','109876543579'),(6,6,'Carlos asd','2005-08-20','1098765432333'),(7,7,'Administrador Principal','1990-05-15','1000000001'),(8,8,'Estudiante de Prueba','2005-06-15','123456789'),(9,9,'Admin de Prueba','1990-05-15','10987654321'),(10,12,'Admin de dea','1990-05-15','10987654321444');
/*!40000 ALTER TABLE `estudiante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estudiante_acudiente`
--

DROP TABLE IF EXISTS `estudiante_acudiente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estudiante_acudiente`
--

LOCK TABLES `estudiante_acudiente` WRITE;
/*!40000 ALTER TABLE `estudiante_acudiente` DISABLE KEYS */;
/*!40000 ALTER TABLE `estudiante_acudiente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `horario`
--

DROP TABLE IF EXISTS `horario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario` (
  `idHorario` int NOT NULL AUTO_INCREMENT,
  `idClase` int NOT NULL,
  `diaSemana` enum('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL,
  `horaInicio` time NOT NULL,
  `horaFin` time NOT NULL,
  PRIMARY KEY (`idHorario`),
  KEY `fk_horario_clase` (`idClase`),
  CONSTRAINT `fk_horario_clase` FOREIGN KEY (`idClase`) REFERENCES `clase` (`idClase`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `horario`
--

LOCK TABLES `horario` WRITE;
/*!40000 ALTER TABLE `horario` DISABLE KEYS */;
INSERT INTO `horario` VALUES (1,1,'MARTES','12:52:00','14:52:00'),(2,1,'MIERCOLES','10:00:00','16:00:00'),(3,1,'VIERNES','02:13:00','14:15:00'),(4,2,'JUEVES','04:14:00','17:17:00');
/*!40000 ALTER TABLE `horario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscripcion`
--

DROP TABLE IF EXISTS `inscripcion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscripcion`
--

LOCK TABLES `inscripcion` WRITE;
/*!40000 ALTER TABLE `inscripcion` DISABLE KEYS */;
INSERT INTO `inscripcion` VALUES (1,10,1,'2026-11-01','ACTIVA'),(2,10,2,'2026-11-02','CANCELADA');
/*!40000 ALTER TABLE `inscripcion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `idPago` int NOT NULL AUTO_INCREMENT,
  `idEstudiante` int NOT NULL,
  `idArchivo` int DEFAULT NULL,
  `monto` decimal(12,2) NOT NULL,
  `fechaPago` date DEFAULT NULL,
  `estado` enum('PENDIENTE','APROBADO','RECHAZADO') NOT NULL,
  PRIMARY KEY (`idPago`),
  KEY `fk_pago_estudiante` (`idEstudiante`),
  KEY `fk_pago_archivo` (`idArchivo`),
  CONSTRAINT `fk_pago_archivo` FOREIGN KEY (`idArchivo`) REFERENCES `archivo` (`idArchivo`),
  CONSTRAINT `fk_pago_estudiante` FOREIGN KEY (`idEstudiante`) REFERENCES `estudiante` (`idEstudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profesor`
--

DROP TABLE IF EXISTS `profesor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profesor`
--

LOCK TABLES `profesor` WRITE;
/*!40000 ALTER TABLE `profesor` DISABLE KEYS */;
INSERT INTO `profesor` VALUES (1,10,'Profesor de Prueba',NULL,NULL,NULL,NULL,'DOCENTE',NULL),(2,11,'María García',NULL,NULL,NULL,NULL,'PROFESIONAL',NULL),(3,17,'Profesor Demo',NULL,NULL,NULL,NULL,'DOCENTE',NULL);
/*!40000 ALTER TABLE `profesor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `programa`
--

DROP TABLE IF EXISTS `programa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `programa` (
  `idPrograma` int NOT NULL AUTO_INCREMENT,
  `nombrePrograma` varchar(100) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `estadoPrograma` enum('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`idPrograma`),
  UNIQUE KEY `nombrePrograma` (`nombrePrograma`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programa`
--

LOCK TABLES `programa` WRITE;
/*!40000 ALTER TABLE `programa` DISABLE KEYS */;
INSERT INTO `programa` VALUES (2,'Bachata','Programa de formacion en bachata','INACTIVO'),(4,'Ballet actualizado','Descripción actualizada para la prueba','ACTIVO'),(7,'Programa de prueba','Creado para comprobar la validación','ACTIVO'),(11,'Danza contemporanea','','ACTIVO');
/*!40000 ALTER TABLE `programa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesion_clase`
--

DROP TABLE IF EXISTS `sesion_clase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sesion_clase` (
  `idSesion` int NOT NULL AUTO_INCREMENT,
  `idClase` int NOT NULL,
  `fecha` date NOT NULL,
  `horaInicio` time NOT NULL,
  `horaFin` time NOT NULL,
  `estado` enum('PROGRAMADA','REALIZADA','CANCELADA') NOT NULL,
  PRIMARY KEY (`idSesion`),
  KEY `fk_sesion_clase_clase` (`idClase`),
  CONSTRAINT `fk_sesion_clase_clase` FOREIGN KEY (`idClase`) REFERENCES `clase` (`idClase`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesion_clase`
--

LOCK TABLES `sesion_clase` WRITE;
/*!40000 ALTER TABLE `sesion_clase` DISABLE KEYS */;
INSERT INTO `sesion_clase` VALUES (1,1,'2026-11-10','12:52:00','14:52:00','CANCELADA'),(2,1,'2026-11-11','10:00:00','16:00:00','REALIZADA');
/*!40000 ALTER TABLE `sesion_clase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `correo` varchar(150) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `rol` enum('ADMINISTRADOR','PROFESOR','ESTUDIANTE') NOT NULL,
  `nombre` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'prueba.estudiante@ejemplo.com',NULL,'$2a$10$N51R.H1Bm7pru57dmY4lP.ZAgPfBS2yDG30ZM4BfDYw5pDZt0xSzW','ESTUDIANTE','Estudiante Prueba'),(2,'estudiante2@ejemplo.com',NULL,'$2a$10$aN1qpLP8qFtLnDi/77wbYuPAapuD0ZmCDBdYw7HlZGPnPebfyfiim','ESTUDIANTE','Estudiante de Prueba'),(3,'carlos.ramirez2026@gmail.com',NULL,'$2a$10$1eHBUMhTQCH2AaHUZAyN2.0gLnhbsT4cAXcQx4hl1v22rTGjeSjT.','ESTUDIANTE','Carlos Ramirez'),(4,'carlos.a@gmail.com',NULL,'$2a$10$BWjnnILvOVUrq0V3XYoWp.E72KRWKEyQpNZ1uV4S0NoOKl/.eHmHC','ESTUDIANTE','Carlos Ramirez'),(5,'carlos.aaa@gmail.com',NULL,'$2a$10$0oKw80CHsLYQGFdu7trtLuihKDEEbTHwOKIIMVOwQT8qE7NNbf2Sy','ESTUDIANTE','Carlos Ramirez'),(6,'admin@arteymovimiento.com',NULL,'$2a$10$pQXjU64I1JeCKAjc4WomPun1pX0/8TOduSiXiOFzNOOy0rfNaLY/u','ADMINISTRADOR','Carlos asd'),(7,'admin@arteymovimientoa.com',NULL,'$2a$10$NRZQdTUgPBtmpbdV264IyOlsHkOWXt8dMJez34tSDS726cnNFW7Fe','ESTUDIANTE','Administrador Principal'),(8,'estudiante@ejemplo.com',NULL,'$2a$10$0oaiu4eWQgVzMhdk1bJOS.AFB/xYu4HKX5Je4jBlj47fkNuaskIZC','ESTUDIANTE','Estudiante de Prueba'),(9,'admin.prueba@ejemplo.com',NULL,'$2a$10$kM3NaWyKo9PfigClCgIX1u1Qo5njkVEH/jlk5sALjBM0TWIrQxfd2','ADMINISTRADOR','Admin de Prueba'),(10,'profesor.prueba@ejemplo.com',NULL,'$2a$10$ZBqAvAY6pdoHelWy8Gmp6u4dJqmM2guYFA48n.SwDbCYoBaH03icK','PROFESOR','Profesor de Prueba'),(11,'maria.garcia@ejemplo.com',NULL,'$2a$10$pPrbf2fZjW4LyvD8Z49MFe1aHpJL2DLTysnIVA.cjspqqRR.sVMfi','PROFESOR','María García'),(12,'estudiante444@ejemplo.com',NULL,'$2a$10$.i06T5qGnTi1.fvvgYRUYut6j4aKZzRBSyX9hzoyZXA4T3EMtHh.G','ESTUDIANTE','Admin de dea'),(15,'admin.pruebas@arteymovimiento.com','3000000000','$2a$10$MS.1ItX47VfnMf0ve1W/T.f.G73P.HamHbNFW5.KeRUu.gppSxm9u','ADMINISTRADOR','Admin de Pruebas'),(16,'admin.demo.am@ejemplo.com','33020301202','$2a$10$39sFoyluOXXpHqXXCKDTluTBQgDx4P7ixx.Fv2JykCTgbOBgSih5G','ADMINISTRADOR','ADMIN DEMO'),(17,'profesor.demo.am@ejemplo.com',NULL,'$2a$10$RJXBruzQBqkgTeJhS7sIOeIIcv8oJsTXEm1i3/eAlPZNRnmxBku1y','PROFESOR','Profesor Demo');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-03 19:28:30
