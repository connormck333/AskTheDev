package com.devconnor.askthedev.security;

import com.devconnor.askthedev.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import({SecurityConfig.class})
class AuthenticationManagerTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "testPassw0rd123%";

    @Test
    void testAuthentication_Successful() {
        UserDetails mockUser = createTestUser();

        when(userDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(mockUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(TEST_EMAIL, TEST_PASSWORD);
        Authentication result = authenticationManager.authenticate(authentication);

        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getName());
    }

    @Test
    void testAuthentication_InvalidCredentials() {
        UserDetails mockUser = createTestUser();

        when(userDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(mockUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(TEST_EMAIL, "invalidPassword");

        assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(authentication));
    }

    @Test
    void testAuthentication_UserNotFound() {
        when(userDetailsService.loadUserByUsername(TEST_EMAIL)).thenThrow(UsernameNotFoundException.class);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(TEST_EMAIL, TEST_PASSWORD);

        assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(authentication));
    }

    private UserDetails createTestUser() {
        return User.builder()
                .username(TEST_EMAIL)
                .password(encoder.encode(TEST_PASSWORD))
                .roles("USER")
                .build();
    }
}
