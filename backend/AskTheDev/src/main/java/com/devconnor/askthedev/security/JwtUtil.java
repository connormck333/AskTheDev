package com.devconnor.askthedev.security;

import com.devconnor.askthedev.models.RefreshToken;
import com.devconnor.askthedev.repositories.RefreshTokenRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;

import static com.devconnor.askthedev.utils.Constants.COOKIE_EXPIRATION_TIME;

@Service
public class JwtUtil {

    private static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final JwtParser jwtParser;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtUtil(RefreshTokenRepository refreshTokenRepository) {
        jwtParser = Jwts.parserBuilder().setSigningKey(JWT_SECRET).build();
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateJwtToken(String userEmail) {
        return generateJwtToken(userEmail, new Date());
    }

    public String generateJwtToken(String userEmail, Date issuedAt) {
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(issuedAt.getTime() + COOKIE_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    public String extractUserEmail(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isSessionValid(HttpServletRequest request, String userEmail) {
        String token = getTokenFromCookie(request);

        if (token == null) return false;

        try {
            return (userEmail.equalsIgnoreCase(extractUserEmail(token)) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public void saveHttpCookie(HttpServletResponse response, String email) {
        String jwtToken = generateJwtToken(email);
        saveRefreshToken(jwtToken);

        ResponseCookie responseCookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(COOKIE_EXPIRATION_TIME)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void saveRefreshToken(String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActive(true);
        refreshToken.setToken(token);

        refreshTokenRepository.save(refreshToken);
    }

    public Key getSecret() {
        return JWT_SECRET;
    }
}
