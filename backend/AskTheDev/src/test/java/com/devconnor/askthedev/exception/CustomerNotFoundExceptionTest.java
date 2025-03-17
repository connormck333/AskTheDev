package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.TestConstants.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class CustomerNotFoundExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Customer not found.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            throw new CustomerNotFoundException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
