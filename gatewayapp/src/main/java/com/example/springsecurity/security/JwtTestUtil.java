package com.example.springsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTestUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long VALIDITY_IN_MILLISECONDS = 3600000;  // 1 час

    // Метод для генерации токена с одной ролью
    public String generateToken(Long userId, String role) {
        return createToken(userId, List.of(role));
    }


    public String generateTokenWithRoles(Long userId, String... roles) {
        return createToken(userId, List.of(roles));
    }

    // Приватный метод для создания токена
    private String createToken(Long userId, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("roles", roles.stream().map(role -> "ROLE_" + role).collect(Collectors.toList()))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Метод для генерации refresh токена
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Метод для получения userId из JWT токена
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
}

