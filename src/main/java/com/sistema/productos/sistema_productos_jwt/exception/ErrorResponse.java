package com.sistema.productos.sistema_productos_jwt.exception;

/**
 * DTO de respuesta de error estandarizado para la API.
 */
public record ErrorResponse(String error, String code) {
}
