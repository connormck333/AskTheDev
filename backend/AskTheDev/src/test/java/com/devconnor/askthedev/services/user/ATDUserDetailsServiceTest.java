package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ATDUserDetailsServiceTest {

    private ATDUserDetailsService atdUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        atdUserDetailsService = new ATDUserDetailsService(userRepository);
    }

    @Test
    void testUserLoadByUsername_Successful() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);
        user.setPassword(PASSWORD);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDetails retrievedUser = atdUserDetailsService.loadUserByUsername(EMAIL);

        assertNotNull(retrievedUser);
        assertEquals(EMAIL, retrievedUser.getUsername());
        assertEquals(PASSWORD, retrievedUser.getPassword());
    }

    @Test
    void testUserLoadByUsername_UserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> atdUserDetailsService.loadUserByUsername(EMAIL));
    }
}
