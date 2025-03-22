package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidUserIdException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.models.UserDTO;
import com.devconnor.askthedev.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void testGetUserById_Successful() {
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        UserDTO retrievedUser = userService.getUserById(userId);

        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getId());
        assertEquals(EMAIL, retrievedUser.getEmail());
        assertEquals(CUSTOMER_ID, retrievedUser.getCustomerId());
    }

    @Test
    void testGetUserById_NotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserIdException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testFindById_Successful() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User retrievedUser = userService.findById(userId);

        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getId());
        assertEquals(EMAIL, retrievedUser.getEmail());
        assertEquals(CUSTOMER_ID, retrievedUser.getCustomerId());
    }

    @Test
    void testFindById_NotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserIdException.class, () -> userService.findById(userId));
    }

    @Test
    void testGetUserByEmail_Successful() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);

        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDTO retrievedUser = userService.getUserByEmail(user.getEmail());

        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getId());
        assertEquals(EMAIL, retrievedUser.getEmail());
        assertEquals(CUSTOMER_ID, retrievedUser.getCustomerId());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(EMAIL));
    }

    @Test
    void testGetUserByCustomerId_Successful() {
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);

        when(userRepository.findUserByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(user));

        UserDTO retrievedUser = userService.getUserByCustomerId(CUSTOMER_ID);

        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getId());
        assertEquals(EMAIL, retrievedUser.getEmail());
        assertEquals(CUSTOMER_ID, retrievedUser.getCustomerId());
    }

    @Test
    void testGetUserByCustomerId_NotFound() {
        when(userRepository.findUserByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> userService.getUserByCustomerId(CUSTOMER_ID));
    }
}
