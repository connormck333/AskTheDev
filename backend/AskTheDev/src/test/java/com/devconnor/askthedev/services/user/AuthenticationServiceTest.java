package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.exception.ExistingUsernameException;
import com.devconnor.askthedev.exception.InvalidSessionException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.RefreshToken;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.RefreshTokenRepository;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication mockedAuthentication;

    @Mock
    private SecurityContext mockedSecurityContext;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                authenticationManager,
                passwordEncoder,
                userRepository,
                refreshTokenRepository,
                jwtUtil,
                userService
        );
        SecurityContextHolder.setContext(mockedSecurityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLogin_Successful() {
        UUID userId = UUID.randomUUID();
        ATDUserResponse atdUserResponse = generateUserResponse(userId);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockedAuthentication);
        doNothing().when(jwtUtil).saveHttpCookies(response, EMAIL);
        when(userService.getATDUserResponseByUser(any())).thenReturn(atdUserResponse);

        ATDUserResponse atdResponse = authenticationService.login(response, EMAIL, PASSWORD);

        assertNotNull(atdResponse);
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
        doNothing().when(jwtUtil).saveHttpCookies(response, EMAIL);

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

    @Test
    void testLogout_Successful() {
        RefreshToken refreshToken = createRefreshToken();

        when(mockedSecurityContext.getAuthentication()).thenReturn(mockedAuthentication);
        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.getTokenFromCookie(request)).thenReturn(SESSION_TOKEN);
        when(refreshTokenRepository.findByToken(SESSION_TOKEN)).thenReturn(refreshToken);

        assertDoesNotThrow(() -> authenticationService.logout(request, response));
    }

    @Test
    void testLogout_WhenNotLoggedIn() {
        when(mockedSecurityContext.getAuthentication()).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authenticationService.logout(request, response));
    }

    @Test
    void testLogout_WhenNotAuthenticated() {
        when(mockedSecurityContext.getAuthentication()).thenReturn(mockedAuthentication);
        when(mockedAuthentication.isAuthenticated()).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> authenticationService.logout(request, response));
    }

    @Test
    void testLogout_WhenRequestSentWithoutToken() {
        when(mockedSecurityContext.getAuthentication()).thenReturn(mockedAuthentication);
        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.getTokenFromCookie(request)).thenReturn(null);

        assertThrows(InvalidSessionException.class, () -> authenticationService.logout(request, response));
    }

    @Test
    void testLogout_WhenRefreshTokenDoesNotExist() {
        when(mockedSecurityContext.getAuthentication()).thenReturn(mockedAuthentication);
        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
        when(jwtUtil.getTokenFromCookie(request)).thenReturn(SESSION_TOKEN);
        when(refreshTokenRepository.findByToken(SESSION_TOKEN)).thenReturn(null);

        assertThrows(InvalidSessionException.class, () -> authenticationService.logout(request, response));
    }
}
