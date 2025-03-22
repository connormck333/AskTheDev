package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.exception.ExistingUsernameException;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String REGISTER_SUCCESS_MESSAGE = "User registered successfully.";
    
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication mockedAuthentication;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                authenticationManager,
                passwordEncoder,
                userRepository,
                jwtUtil
        );
    }

    @Test
    void testLogin_Successful() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockedAuthentication);
        doNothing().when(jwtUtil).saveHttpCookie(response, EMAIL);

        boolean loginResponse = authenticationService.login(response, EMAIL, PASSWORD);

        assertTrue(loginResponse);
    }

    @Test
    void testLogin_Unsuccessful() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(response, EMAIL, PASSWORD));
    }

    @Test
    void testRegister_Successful() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);

        when(userRepository.existsUserByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(jwtUtil).saveHttpCookie(response, EMAIL);

        ATDUserResponse atdUserResponse = authenticationService.register(response, EMAIL, PASSWORD);

        assertNotNull(atdUserResponse);
        assertEquals(EMAIL, atdUserResponse.getEmail());
        assertEquals(userId, atdUserResponse.getUserId());
        assertEquals(REGISTER_SUCCESS_MESSAGE, atdUserResponse.getMessage());
    }

    @Test
    void testRegister_ExistingUsername() {
        when(userRepository.existsUserByEmail(EMAIL)).thenReturn(true);

        assertThrows(ExistingUsernameException.class, () -> authenticationService.register(response, EMAIL, PASSWORD));
    }
}
