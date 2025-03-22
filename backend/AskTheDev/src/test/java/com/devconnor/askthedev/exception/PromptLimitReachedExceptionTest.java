package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class PromptLimitReachedExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Prompt limit reached.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(PromptLimitReachedException.class, () -> {
            throw new PromptLimitReachedException();
        });


        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
