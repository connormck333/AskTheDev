package com.devconnor.askthedev.security;

import com.devconnor.askthedev.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private static final String TEST_EMAIL = "test@gmail.com";

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
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);
        Cookie cookie = new Cookie("token", token);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        boolean isSessionValid = jwtUtil.isSessionValid(request, TEST_EMAIL);

        assertTrue(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithInvalidToken() {
        String token = "invalidToken";
        Cookie cookie = new Cookie("token", token);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        boolean isSessionValid = jwtUtil.isSessionValid(request, TEST_EMAIL);

        assertFalse(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithInvalidEmail() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);
        Cookie cookie = new Cookie("token", token);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        boolean isSessionValid = jwtUtil.isSessionValid(request, "invalidEmail");

        assertFalse(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithExpiredToken() {
        Date invalidDate = new Date();
        invalidDate.setTime(invalidDate.getTime() - (1000 * 60 * 60 * 24 * 5));
        String token = jwtUtil.generateJwtToken(TEST_EMAIL, invalidDate);
        Cookie cookie = new Cookie("token", token);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        boolean isSessionValid = jwtUtil.isSessionValid(request, TEST_EMAIL);

        assertFalse(isSessionValid);
    }

    @Test
    void testIsSessionValid_WithEmptyCookies() {
        when(request.getCookies()).thenReturn(null);

        boolean isSessionValid = jwtUtil.isSessionValid(request, "invalidEmail");

        assertFalse(isSessionValid);
    }

    @Test
    void testSaveHttpCookie_Successful() {
        jwtUtil.saveHttpCookie(response, TEST_EMAIL);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), captor.capture());

        String cookie = captor.getValue();

        assertNotNull(cookie);
        assertTrue(cookie.contains("token="));
        assertTrue(cookie.contains("HttpOnly"));
    }

    @Test
    void testGetTokenFromCookie_Successful() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);
        Cookie cookie = new Cookie("token", token);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String retrievedToken = jwtUtil.getTokenFromCookie(request);

        assertNotNull(retrievedToken);
        assertEquals(token, retrievedToken);
    }

    @Test
    void testGetTokenFromCookie_WithEmptyCookies() {
        when(request.getCookies()).thenReturn(null);

        String retrievedToken = jwtUtil.getTokenFromCookie(request);

        assertNull(retrievedToken);
    }

    @Test
    void testGetTokenFromCookie_WithMissingCookie() {
        String token = jwtUtil.generateJwtToken(TEST_EMAIL);
        Cookie cookie = new Cookie("differentToken", token);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String retrievedToken = jwtUtil.getTokenFromCookie(request);

        assertNull(retrievedToken);
    }
}
