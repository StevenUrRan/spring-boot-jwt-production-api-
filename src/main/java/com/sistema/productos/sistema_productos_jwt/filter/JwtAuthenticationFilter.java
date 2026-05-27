package com.sistema.productos.sistema_productos_jwt.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.exception.ErrorResponse;
import com.sistema.productos.sistema_productos_jwt.service.jwt.CustomUserDetailsService;
import com.sistema.productos.sistema_productos_jwt.service.jwt.JwtServiceInterface;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * Filtro que valida el token JWT en cada petición entrante.
 * Si el token está expirado responde directamente con 401 y un JSON estructurado.
 */
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private CustomUserDetailsService userDetailsService;
    private JwtServiceInterface jwtService;
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.info("JWT Authentication Filter - Validando request");
        logger.info("Endpoint: {} {}", request.getMethod(), request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header o formato inválido (Bearer token requerido)");
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("Authorization header encontrado");
        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractUsername(token);
            logger.info("Email del token: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("Cargando detalles del usuario...");
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isValid(token, userDetails)) {
                    logger.info("Token válido - Estableciendo autenticación en SecurityContext");
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("Usuario autenticado: {} con roles: {}", email, userDetails.getAuthorities());
                } else {
                    logger.warn("Token inválido para el usuario {}", email);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado para la solicitud: {}", request.getRequestURI());
            writeUnauthorizedResponse(response, "Token expirado", "TOKEN_EXPIRED");
            return;
        } catch (JwtException e) {
            logger.warn("Token JWT inválido: {}", e.getMessage());
            writeUnauthorizedResponse(response, "Token inválido", "TOKEN_INVALID");
            return;
        } catch (Exception e) {
            logger.error("Error inesperado durante validación de token: {}", e.getMessage());
        }

        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        filterChain.doFilter(request, response);
    }

    /**
     * Escribe una respuesta 401 con cuerpo JSON estructurado directamente en el
     * HttpServletResponse, cortocircuitando la cadena de filtros.
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String error, String code)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(error, code);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
