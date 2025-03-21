package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.TestConstants.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class InvalidPromptExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Invalid prompt.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(InvalidPromptException.class, () -> {
            throw new InvalidPromptException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
