package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class InvalidSubscriptionExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Invalid subscription.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(InvalidSubscriptionException.class, () -> {
            throw new InvalidSubscriptionException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
