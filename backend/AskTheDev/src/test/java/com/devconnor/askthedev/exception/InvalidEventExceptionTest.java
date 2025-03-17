package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.TestConstants.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class InvalidEventExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Invalid event.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(InvalidEventException.class, () -> {
            throw new InvalidEventException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
