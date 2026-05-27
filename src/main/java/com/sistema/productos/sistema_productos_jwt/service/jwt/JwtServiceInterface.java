package com.sistema.productos.sistema_productos_jwt.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;

public interface JwtServiceInterface {
    String generateToken(UserDetails user);
    String extractUsername(String token);
    Date extractExpiration(String token);
    boolean isValid(String token, UserDetails user);
}
