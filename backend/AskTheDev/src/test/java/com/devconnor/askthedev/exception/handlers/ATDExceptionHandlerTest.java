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
    @MethodSource("handleBadRequestExceptionContent")
    void handleNotFoundException(ATDException e, String message) {
        when(e.getMessage()).thenReturn(message);
        ATDErrorResponse response = handler.handleBadRequestException(e);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
    }

    private static Stream<Arguments> handleBadRequestExceptionContent() {
        return Stream.of(
                Arguments.of(mock(CustomerNotFoundException.class), "Customer not found."),
                Arguments.of(mock(UserNotFoundException.class), "User not found."),
                Arguments.of(mock(SubscriptionNotFoundException.class), "Subscription not found."),
                Arguments.of(mock(ExistingUsernameException.class), "A user with this email already exists.")
        );
    }

    @ParameterizedTest
    @MethodSource("handleInvalidExceptionContent")
    void handleInvalidException(ATDException e, String message) {
        when(e.getMessage()).thenReturn(message);
        ATDErrorResponse response = handler.handleInvalidException(e);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
    }

    private static Stream<Arguments> handleInvalidExceptionContent() {
        return Stream.of(
                Arguments.of(mock(InvalidUserIdException.class), "Invalid user ID: %s"),
                Arguments.of(mock(InvalidEventException.class), "Invalid event."),
                Arguments.of(mock(InvalidPromptException.class), "Invalid prompt.")
        );
    }

    @ParameterizedTest
    @MethodSource("handleForbiddenExceptionContent")
    void handleInvalidSessionException(ATDException e, String message) {
        when(e.getMessage()).thenReturn(message);

        ATDErrorResponse response = handler.handleInvalidSessionException(e);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
    }

    private static Stream<Arguments> handleForbiddenExceptionContent() {
        return Stream.of(
                Arguments.of(mock(InvalidSessionException.class), "Invalid session."),
                Arguments.of(mock(InvalidSubscriptionException.class), "Invalid subscription."),
                Arguments.of(mock(PromptLimitReachedException.class), "Prompt limit reached.")
        );
    }
}
