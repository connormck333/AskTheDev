package com.devconnor.askthedev.security;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import({SecurityConfig.class})
class CorsConfigurationSourceTest {

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:5173",
            "chrome-extension://lnnbjajgiifocmcfifoeccdilelmibdf"
    );
    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "DELETE"
    );

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletMapping mapping;

    @Test
    void testCorsConfigurationSource() {
        when(request.getContextPath()).thenReturn("/");
        when(request.getRequestURI()).thenReturn("/");
        when(request.getHttpServletMapping()).thenReturn(mapping);
        when(request.getServletPath()).thenReturn("/");
        assertNotNull(corsConfigurationSource);

        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) corsConfigurationSource;
        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        List<String> allowedOrigins = config.getAllowedOrigins();
        assertNotNull(allowedOrigins);
        assertTrue(allowedOrigins.containsAll(ALLOWED_ORIGINS));

        List<String> allowedMethods = config.getAllowedMethods();
        assertNotNull(allowedMethods);
        assertTrue(allowedMethods.containsAll(ALLOWED_METHODS));

        Boolean allowCredentials = config.getAllowCredentials();
        assertNotNull(allowCredentials);
        assertTrue(allowCredentials);
    }
}
