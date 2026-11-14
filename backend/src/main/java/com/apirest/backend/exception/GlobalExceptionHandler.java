package com.apirest.backend.exception;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.apirest.backend.dto.ApiErrorDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(CredencialesInvalidasException.class)
        public ResponseEntity<ApiErrorDTO> manejarCredencialesInvalidas(
                        CredencialesInvalidasException ex,
                        HttpServletRequest request) {

                return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
        }

        // Error 403: el usuario autenticado no puede gestionar este recurso
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiErrorDTO> manejarAccesoDenegado(
                        AccessDeniedException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.FORBIDDEN,
                                "No tienes permiso para realizar esta operación.",
                                request);
        }

        // Error 404: recurso no encontrado
        @ExceptionHandler(RecursoNoEncontradoException.class)
        public ResponseEntity<ApiErrorDTO> manejarRecursoNoEncontrado(
                        RecursoNoEncontradoException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage(),
                                request);
        }

        // Error 409: recurso duplicado
        @ExceptionHandler(RecursoDuplicadoException.class)
        public ResponseEntity<ApiErrorDTO> manejarRecursoDuplicado(
                        RecursoDuplicadoException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.CONFLICT,
                                ex.getMessage(),
                                request);
        }

        // Error 400: archivo ausente, vacío o con ruta no válida
        @ExceptionHandler(ArchivoInvalidoException.class)
        public ResponseEntity<ApiErrorDTO> manejarArchivoInvalido(
                        ArchivoInvalidoException ex,
                        HttpServletRequest request) {

                return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        }

        // Error 413: archivo demasiado grande
        @ExceptionHandler(ArchivoDemasiadoGrandeException.class)
        public ResponseEntity<ApiErrorDTO> manejarArchivoDemasiadoGrande(
                        ArchivoDemasiadoGrandeException ex,
                        HttpServletRequest request) {

                return construirRespuesta(HttpStatus.CONTENT_TOO_LARGE, ex.getMessage(), request);
        }

        // Error 413: Spring rechazó la subida por superar el límite multipart configurado
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiErrorDTO> manejarLimiteMultipart(
                        MaxUploadSizeExceededException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.CONTENT_TOO_LARGE,
                                "El archivo supera el tamaño máximo permitido.",
                                request);
        }

        // Error 415: tipo de archivo no permitido
        @ExceptionHandler(TipoArchivoNoPermitidoException.class)
        public ResponseEntity<ApiErrorDTO> manejarTipoArchivoNoPermitido(
                        TipoArchivoNoPermitidoException ex,
                        HttpServletRequest request) {

                return construirRespuesta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), request);
        }

        // Error 400: datos enviados que no cumplen las validaciones
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorDTO> manejarErrorValidacion(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                String mensaje = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Los datos enviados no son válidos.");

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                mensaje,
                                request);
        }

        // Error 400: JSON inválido o valor no permitido para un campo
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorDTO> manejarJsonInvalido(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                "El JSON enviado tiene un formato o valor no válido.",
                                request);
        }

        // Error 404: ruta sin un endpoint asociado
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiErrorDTO> manejarRutaNoEncontrada(
                        NoResourceFoundException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.NOT_FOUND,
                                "No existe un endpoint para la ruta solicitada.",
                                request);
        }

        // Error 409: conflicto con restricciones de la base de datos
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiErrorDTO> manejarErrorIntegridad(
                        DataIntegrityViolationException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.CONFLICT,
                                "La operación no se puede completar porque los datos entran en conflicto con la información registrada.",
                                request);
        }

        // Error 400: parámetro de ruta o consulta con tipo o valor no válido
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiErrorDTO> manejarParametroInvalido(
                        MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                "El valor enviado para el parámetro '" + ex.getName() + "' no es válido.",
                                request);
        }

        // Error 500: cualquier error inesperado
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorDTO> manejarErrorGeneral(
                        Exception ex,
                        HttpServletRequest request) {

                logger.error("Se produjo un error inesperado en la API", ex);

                return construirRespuesta(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Ocurrió un error inesperado en el servidor.",
                                request);
        }

        // Método auxiliar para construir todas las respuestas de error
        private ResponseEntity<ApiErrorDTO> construirRespuesta(
                        HttpStatus status,
                        String mensaje,
                        HttpServletRequest request) {

                ApiErrorDTO error = new ApiErrorDTO(
                                LocalDateTime.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                mensaje,
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

        // Error 400: solicitud con datos incorrectos
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorDTO> manejarSolicitudInvalida(
                        IllegalArgumentException ex,
                        HttpServletRequest request) {

                return construirRespuesta(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage(),
                                request);
        }
}
