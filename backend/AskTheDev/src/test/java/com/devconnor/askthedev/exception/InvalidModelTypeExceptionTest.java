package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvalidModelTypeExceptionTest {

    private static final String EXCEPTION_MESSAGE = "Invalid model type: invalidModelType";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(InvalidModelTypeException.class, () -> {
            throw new InvalidModelTypeException("invalidModelType");
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
