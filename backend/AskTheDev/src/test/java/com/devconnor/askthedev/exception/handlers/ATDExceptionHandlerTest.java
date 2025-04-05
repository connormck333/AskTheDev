package com.devconnor.askthedev.exception.handlers;

import com.devconnor.askthedev.exception.*;
import com.devconnor.askthedev.models.ATDErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ATDExceptionHandlerTest {

    private static final String ERROR_PREFIX = "[ATD] ERROR: %s";

    private ATDExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ATDExceptionHandler();
    }

    @Test
    void handleATDException() {
        ATDException exception = new ATDException();

        ResponseEntity<ATDErrorResponse> response = handler.handleATDException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertExceptionMessage("An unknown error occurred.", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentialsException() {
        BadCredentialsException exception = new BadCredentialsException("Invalid email or password.");

        ResponseEntity<ATDErrorResponse> response = handler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid email or password.", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("handleBadRequestExceptionContent")
    void handleNotFoundException(ATDException e, String message) {
        ResponseEntity<ATDErrorResponse> response = handler.handleBadRequestException(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertExceptionMessage(message, response.getBody().getMessage());
    }

    private static Stream<Arguments> handleBadRequestExceptionContent() {
        return Stream.of(
                Arguments.of(new CustomerNotFoundException(), "Customer not found."),
                Arguments.of(new UserNotFoundException(), "User not found."),
                Arguments.of(new SubscriptionNotFoundException(), "Subscription not found."),
                Arguments.of(new ExistingUsernameException(), "A user with this email already exists.")
        );
    }

    @ParameterizedTest
    @MethodSource("handleInvalidExceptionContent")
    void handleInvalidException(ATDException e, String message) {
        ResponseEntity<ATDErrorResponse> response = handler.handleInvalidException(e);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertExceptionMessage(message, response.getBody().getMessage());
    }

    private static Stream<Arguments> handleInvalidExceptionContent() {
        UUID userId = UUID.randomUUID();
        return Stream.of(
                Arguments.of(new InvalidUserIdException(userId), String.format("Invalid user ID: %s", userId)),
                Arguments.of(new InvalidEventException(), "Invalid event."),
                Arguments.of(new InvalidPromptException(), "Invalid prompt."),
                Arguments.of(new InvalidModelTypeException("invalidModelType"), "Invalid model type: invalidModelType")
        );
    }

    @ParameterizedTest
    @MethodSource("handleForbiddenExceptionContent")
    void handleInvalidSessionException(ATDException e, String message) {
        ResponseEntity<ATDErrorResponse> response = handler.handleInvalidSessionException(e);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertExceptionMessage(message, response.getBody().getMessage());
    }

    private static Stream<Arguments> handleForbiddenExceptionContent() {
        return Stream.of(
                Arguments.of(new InvalidSessionException(), "Invalid session."),
                Arguments.of(new InvalidSubscriptionException(), "Invalid subscription."),
                Arguments.of(new PromptLimitReachedException(), "Prompt limit reached."),
                Arguments.of(new TermsNotAcceptedException(), "You must accept the Terms & Conditions to create an account.")
        );
    }

    @Test
    void handleExistingUsernameException() {
        ExistingUsernameException exception = new ExistingUsernameException();

        ResponseEntity<ATDErrorResponse> response = handler.handleExistingUsernameException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertExceptionMessage("A user with this email already exists.", response.getBody().getMessage());
    }

    private static void assertExceptionMessage(String expectedMessage, String actualMessage) {
        assertEquals(String.format(ERROR_PREFIX, expectedMessage), actualMessage);
    }
}
