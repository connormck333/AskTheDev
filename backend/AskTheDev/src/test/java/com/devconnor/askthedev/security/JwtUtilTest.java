package com.devconnor.askthedev.security;

import com.devconnor.askthedev.models.RefreshToken;
import com.devconnor.askthedev.repositories.RefreshTokenRepository;
import com.devconnor.askthedev.utils.EnvUtils;
import com.devconnor.askthedev.utils.EnvironmentType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Date;

import static com.devconnor.askthedev.utils.Utils.createRefreshToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private HttpServletRequest request;

    private static final String TEST_EMAIL = "test@gmail.com";

    @BeforeAll
    static void setup() {
        EnvUtils.loadDotEnv(EnvironmentType.LOCAL);
    }

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(refreshTokenRepository);
    }

    @Test
    void testGenerateJwtToken_Successful() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);
        Key secret = jwtUtil.getSecret();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertNotNull(token);
        assertEquals(TEST_EMAIL, claims.getSubject());

        Date now = new Date();
        assertTrue(claims.getIssuedAt().before(now) || claims.getIssuedAt().equals(now));
        assertTrue(claims.getExpiration().after(now));
    }

    @Test
    void testExtractUserEmail_Successful() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);

        String extractedEmail = jwtUtil.extractUserEmail(token);

        assertEquals(TEST_EMAIL, extractedEmail);
    }

    @Test
    void testExtractUserEmail_WithInvalidToken() {
        String token = "invalidToken";

        assertThrows(MalformedJwtException.class, () -> jwtUtil.extractUserEmail(token));
    }

    @Test
    void testIsSessionValid_Successful() {
        String jwtToken = jwtUtil.generateJwtToken(TEST_EMAIL);
        RefreshToken refreshToken = createRefreshToken();

        when(request.getHeader("Authorization")).thenReturn(jwtToken);
        when(refreshTokenRepository.findByToken(jwtToken)).thenReturn(refreshToken);

        boolean isSessionValid = jwtUtil.isSessionValid(request, TEST_EMAIL);

        assertTrue(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithInvalidToken() {
        String token = "invalidToken";

        when(request.getHeader("Authorization")).thenReturn(token);

        boolean isSessionValid = jwtUtil.isSessionValid(request, TEST_EMAIL);

        assertFalse(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithInvalidEmail() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);
        RefreshToken refreshToken = createRefreshToken();

        when(request.getHeader("Authorization")).thenReturn(token);
        when(refreshTokenRepository.findByToken(token)).thenReturn(refreshToken);

        boolean isSessionValid = jwtUtil.isSessionValid(request, "invalidEmail");

        assertFalse(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithExpiredToken() {
        Date invalidDate = new Date();
        invalidDate.setTime(invalidDate.getTime() - (1000 * 60 * 60 * 24 * 5));
        String token = Jwts.builder()
                .setSubject(TEST_EMAIL)
                .setIssuedAt(invalidDate)
                .setExpiration(new Date(invalidDate.getTime() - 100))
                .signWith(SignatureAlgorithm.HS256, jwtUtil.getSecret())
                .compact();
        RefreshToken refreshToken = createRefreshToken();

        when(request.getHeader("Authorization")).thenReturn(token);
        when(refreshTokenRepository.findByToken(token)).thenReturn(refreshToken);

        boolean isSessionValid = jwtUtil.isSessionValid(request, TEST_EMAIL);

        assertFalse(isSessionValid);
    }

    @Test
    void testIsSessionValid_AuthorizationHeaderEmpty() {
        when(request.getHeader("Authorization")).thenReturn(null);

        boolean isSessionValid = jwtUtil.isSessionValid(request, "invalidEmail");

        assertFalse(isSessionValid);
    }

    @Test
    void testGetAuthToken_Successful() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);

        when(request.getHeader("Authorization")).thenReturn(token);

        String retrievedToken = jwtUtil.getAuthToken(request);

        assertNotNull(retrievedToken);
        assertEquals(token, retrievedToken);
    }
}
