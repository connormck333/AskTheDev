package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.TestConstants.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionNotFoundExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Subscription not found.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            throw new SubscriptionNotFoundException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
