package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class ExistingUsernameExceptionTest {

    private static final String EXCEPTION_MESSAGE = "A user with this email already exists.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(ExistingUsernameException.class, () -> {
            throw new ExistingUsernameException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
