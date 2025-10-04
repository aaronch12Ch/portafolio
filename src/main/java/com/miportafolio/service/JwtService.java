package com.miportafolio.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;


    /**
     * Genera un token JWT para un usuario autenticado.
     * Incluye los roles del usuario como 'claims'.
     * @param authentication Objeto de autenticación de Spring Security.
     * @return Token JWT como String.
     */
    public String generarToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Extrae los roles (Authorities) y los convierte a una lista de Strings
        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .addClaims(claims) // Añade los roles como claims
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ===============================================
    // Métodos de Validación y Extracción
    // ===============================================

    /**
     * Valida si un token JWT es válido (no expirado y coincide con la firma)
     * para un UserDetails específico.
     * @param token Token JWT a validar.
     * @param userDetails Detalles del usuario.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractRoles(String token) {
        // La clave 'roles' debe coincidir con la clave que usaste en generarToken()
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }
    /**
     * Extrae el nombre de usuario (subject) del token.
     * @param token Token JWT.
     * @return Nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ===============================================
    // Métodos Privados
    // ===============================================

    /**
     * Extrae un claim específico del token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Verifica si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    /**
     * Extrae la fecha de expiración del token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todos los claims del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    /**
     * Obtiene la clave de firma (secret key) decodificada.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
