package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class ATDExceptionTest {

    @Test
    void testThrowsException()  {
        assertThrows(ATDException.class, () -> {
            throw new ATDException();
        });
    }

    @Test
    void testThrowsWithMessage() {
        String exceptionMessage = "exception message";
        Exception exception = assertThrows(ATDException.class, () -> {
           throw new ATDException(exceptionMessage);
        });

        assertEquals(String.format(EXCEPTION_PREFIX, exceptionMessage), exception.getMessage());
    }
}
