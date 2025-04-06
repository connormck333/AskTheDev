package com.devconnor.askthedev.security;

import com.devconnor.askthedev.models.RefreshToken;
import com.devconnor.askthedev.repositories.RefreshTokenRepository;
import com.devconnor.askthedev.utils.EnvUtils;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.devconnor.askthedev.utils.Constants.AUTH_EXPIRATION_TIME;

@Service
public class JwtUtil {

    private static final String JWT_SECRET_KEY = "JWT_SECRET_KEY";

    private final Key jwtSecret;
    private final JwtParser jwtParser;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtUtil(RefreshTokenRepository refreshTokenRepository) {
        byte[] keyBytes = Base64.getDecoder().decode(EnvUtils.loadString(JWT_SECRET_KEY));
        jwtSecret = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(jwtSecret).build();
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateJwtToken(String userEmail) {
        Date issuedAt = new Date();
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(issuedAt.getTime() + AUTH_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String extractUserEmail(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isSessionValid(HttpServletRequest request, String userEmail) {
        String jwtToken = getAuthToken(request);
        if (jwtToken == null) return false;

        RefreshToken refreshToken = refreshTokenRepository.findByToken(jwtToken);
        if (refreshToken == null || !refreshToken.isActive()) return false;

        try {
            return userEmail.equalsIgnoreCase(extractUserEmail(jwtToken))
                    && !isTokenExpired(jwtToken);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public String createJwtToken(String email) {
        String jwtToken = generateJwtToken(email);
        saveRefreshToken(jwtToken);

        return jwtToken;
    }

    public String getAuthToken(HttpServletRequest request) {
        try {
            return request.getHeader("Authorization").replace("Bearer ", "");
        } catch (Exception e) {
            return null;
        }
    }

    private void saveRefreshToken(String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActive(true);
        refreshToken.setToken(token);

        refreshTokenRepository.save(refreshToken);
    }

    public Key getSecret() {
        return jwtSecret;
    }
}
