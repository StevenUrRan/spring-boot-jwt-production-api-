package com.sistema.productos.sistema_productos_jwt.exception;

/**
 * Excepción lanzada cuando el token JWT ha expirado.
 * Resulta en una respuesta HTTP 401 Unauthorized con un cuerpo JSON estructurado.
 */
public class JwtExpiredException extends RuntimeException {

    public JwtExpiredException(String message) {
        super(message);
    }

    public JwtExpiredException() {
        super("El token JWT ha expirado.");
    }
}
