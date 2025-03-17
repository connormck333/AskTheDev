package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.TestConstants.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    private static final String EXCEPTION_MESSAGE = "User with email %s not found.";

    @Test
    void testThrowsException() {
        String email = "test@gmail.com";
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            throw new UserNotFoundException(email);
        });

        String expectedMessage = String.format(EXCEPTION_MESSAGE, email);

        assertEquals(
                String.format(EXCEPTION_PREFIX, expectedMessage),
                exception.getMessage()
        );
    }
}
