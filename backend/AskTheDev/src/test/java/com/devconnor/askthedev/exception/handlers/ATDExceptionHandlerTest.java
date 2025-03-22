package com.devconnor.askthedev.exception.handlers;

import com.devconnor.askthedev.exception.*;
import com.devconnor.askthedev.models.ATDErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ATDExceptionHandlerTest {

    private ATDExceptionHandler handler;

    @Mock
    private ATDException atdException;

    @Mock
    private ExistingUsernameException existingUsernameException;

    @Mock
    private InvalidSessionException invalidSessionException;

    @BeforeEach
    void setUp() {
        handler = new ATDExceptionHandler();
    }

    @Test
    void handleATDException() {
        ATDErrorResponse response = handler.handleATDException(atdException);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("[ATD] Error: An error occurred.", response.getMessage());
    }

    @ParameterizedTest
    @MethodSource("handleNotFoundExceptionContent")
    void handleNotFoundException(ATDException e, String message) {
        when(e.getMessage()).thenReturn(message);
        ATDErrorResponse response = handler.handleNotFoundException(e);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
    }

    private static Stream<Arguments> handleNotFoundExceptionContent() {
        return Stream.of(
                Arguments.of(mock(CustomerNotFoundException.class), "Customer not found."),
                Arguments.of(mock(UserNotFoundException.class), "User not found."),
                Arguments.of(mock(SubscriptionNotFoundException.class), "Subscription not found.")
        );
    }

    @Test
    void handleExistingUsernameException() {
        when(existingUsernameException.getMessage()).thenReturn("A user with this email already exists.");

        ATDErrorResponse response = handler.handleExistingUsernameException(existingUsernameException);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals("A user with this email already exists.", response.getMessage());
    }

    @ParameterizedTest
    @MethodSource("handleInvalidExceptionContent")
    void handleInvalidException(ATDException e, String message) {
        when(e.getMessage()).thenReturn(message);
        ATDErrorResponse response = handler.handleInvalidException(e);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
    }

    @Test
    void handleInvalidSessionException() {
        when(invalidSessionException.getMessage()).thenReturn("Invalid session.");

        ATDErrorResponse response = handler.handleInvalidSessionException(invalidSessionException);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode());
        assertEquals("Invalid session.", response.getMessage());
    }

    private static Stream<Arguments> handleInvalidExceptionContent() {
        return Stream.of(
                Arguments.of(mock(InvalidUserIdException.class), "Invalid user ID: %s"),
                Arguments.of(mock(InvalidEventException.class), "Invalid event."),
                Arguments.of(mock(InvalidPromptException.class), "Invalid prompt.")
        );
    }
}
