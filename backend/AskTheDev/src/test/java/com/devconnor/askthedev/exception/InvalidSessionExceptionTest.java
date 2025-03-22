package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class InvalidSessionExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Invalid session.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(InvalidSessionException.class, () -> {
            throw new InvalidSessionException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
