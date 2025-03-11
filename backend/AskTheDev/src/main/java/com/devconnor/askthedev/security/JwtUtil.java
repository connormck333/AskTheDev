package com.devconnor.askthedev.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtUtil {

    private static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = (long) 1000 * 60 * 60 * 24;

    private final JwtParser jwtParser;

    public JwtUtil() {
        jwtParser = Jwts.parserBuilder().setSigningKey(JWT_SECRET).build();
    }

    public String generateJwtToken(String userEmail) {
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    public String extractUserEmail(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, String userEmail) {
        return (userEmail.equals(extractUserEmail(token)) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
