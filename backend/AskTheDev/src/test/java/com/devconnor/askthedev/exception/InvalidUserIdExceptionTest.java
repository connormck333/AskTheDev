package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class InvalidUserIdExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Invalid user ID: %s";

    @Test
    void testThrowsException()  {
        UUID uuid = UUID.randomUUID();
        Exception exception = assertThrows(InvalidUserIdException.class, () -> {
            throw new InvalidUserIdException(uuid);
        });

        String expectedMessage = String.format(EXCEPTION_MESSAGE, uuid);

        assertEquals(
                String.format(EXCEPTION_PREFIX, expectedMessage),
                exception.getMessage()
        );
    }
}
