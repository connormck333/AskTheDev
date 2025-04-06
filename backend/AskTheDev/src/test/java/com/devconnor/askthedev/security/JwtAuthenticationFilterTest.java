package com.devconnor.askthedev.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/auth/login", "/auth/signup"})
    void testShouldNotFilter_LoginAndSignupEndpoints(String endpoint) {
        request.setRequestURI(endpoint);
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
    }

    @Test
    void testShouldFilter_OtherEndpoints() {
        request.setRequestURI("/payment/create-checkout");
        assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        when(jwtUtil.getAuthToken(request)).thenReturn("validToken");
        when(jwtUtil.extractUserEmail("validToken")).thenReturn("test@gmail.com");
        when(jwtUtil.isSessionValid(request, "test@gmail.com")).thenReturn(true);
        request.setRequestURI("/payment/create-checkout");
        request.setMethod("POST");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithMissingToken() throws ServletException, IOException {
        request.setRequestURI("/payment/create-checkout");
        request.setMethod("POST");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        when(jwtUtil.getAuthToken(request)).thenReturn("invalidToken");
        when(jwtUtil.extractUserEmail("invalidToken")).thenReturn("test@gmail.com");
        when(jwtUtil.isSessionValid(request, "test@gmail.com")).thenReturn(false);

        request.setRequestURI("/payment/create-checkout");
        request.setMethod("POST");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNullEmail() throws ServletException, IOException {
        when(jwtUtil.getAuthToken(request)).thenReturn("invalidToken");
        when(jwtUtil.extractUserEmail("invalidToken")).thenReturn(null);

        request.setRequestURI("/payment/create-checkout");
        request.setMethod("POST");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
