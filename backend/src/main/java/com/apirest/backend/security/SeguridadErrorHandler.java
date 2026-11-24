package com.apirest.backend.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Los errores 401 (sin token o token inválido) y 403 (rol insuficiente) los detecta Spring Security en sus filtros,
 * antes de llegar a un controlador, y por defecto responden con el cuerpo vacío. Aquí se delegan en el
 * GlobalExceptionHandler para que salgan con el mismo JSON (ApiErrorDTO) que el resto de errores de la API.
 */
@Component
public class SeguridadErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    private final BearerTokenAuthenticationEntryPoint bearer = new BearerTokenAuthenticationEntryPoint();

    // Constructor escrito a mano: Lombok no copia @Qualifier al parámetro, y Spring tiene
    // varios HandlerExceptionResolver; se necesita el que delega en los @ExceptionHandler
    public SeguridadErrorHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        bearer.commence(request, response, ex); // conserva la cabecera estándar WWW-Authenticate del 401
        resolver.resolveException(request, response, null, ex);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
        resolver.resolveException(request, response, null, ex);
    }
}
