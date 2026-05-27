package com.sistema.productos.sistema_productos_jwt.service.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSecretKey() {
        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("La llave JWT no ha sido cargada");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new RuntimeException("La llave JWT debe tener al menos 32 bytes (256 bits)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(UserDetails user) {
        logger.info("Generando token JWT para usuario: {}", user.getUsername());
        String token = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
        logger.info("Token generado exitosamente");
        return token;
    }

    private Claims extractAllClaims(String token) {
        try {
            logger.debug("Validando firma del token JWT...");
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            logger.debug("Firma del token validada correctamente");
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Token inválido o expirado: {}", e.getMessage());
            throw new RuntimeException("Token JWT inválido o expirado: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean isValid(String token, UserDetails user) {
        try {
            logger.info("🔎 Validando token para usuario: {}", user.getUsername());
            String username = extractUsername(token);
            boolean isExpired = isTokenExpired(token);
            boolean usernameMatches = username.equals(user.getUsername());
            logger.info("   - Usuario en token: {}", username);
            logger.info("   - Usuario esperado: {}", user.getUsername());
            logger.info("   - ¿Usuario coincide?: {}", usernameMatches);
            logger.info("   - ¿Token expirado?: {}", isExpired);
            boolean isTokenValid = usernameMatches && !isExpired;
            logger.info("   ➜ Resultado final: {} {}", isTokenValid ? "✅ VÁLIDO" : "❌ INVÁLIDO",
                    isTokenValid ? "- Token autenticado correctamente" : "");
            return isTokenValid;
        } catch (Exception e) {
            logger.error("❌ Error en validación de token: {}", e.getMessage());
            return false;
        }
    }
}
