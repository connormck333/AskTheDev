package com.devconnor.askthedev.exception;

import org.junit.jupiter.api.Test;

import static com.devconnor.askthedev.utils.Utils.EXCEPTION_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TermsNotAcceptedExceptionTest {

    private static final String EXCEPTION_MESSAGE = "You must accept the Terms & Conditions to create an account.";

    @Test
    void testThrowsException()  {
        Exception exception = assertThrows(TermsNotAcceptedException.class, () -> {
            throw new TermsNotAcceptedException();
        });

        assertEquals(
                String.format(EXCEPTION_PREFIX, EXCEPTION_MESSAGE),
                exception.getMessage()
        );
    }
}
