package com.devconnor.askthedev.exception.handlers;

import com.devconnor.askthedev.exception.*;
import com.devconnor.askthedev.models.ATDErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ATDExceptionHandler {

    @ExceptionHandler(value = {Exception.class, ATDException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ATDErrorResponse> handleATDException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ATDErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage().contains("[ATD] ERROR") ? e.getMessage() : "An error occurred."
        ));
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ATDErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ATDErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid email or password."
        ));
    }

    @ExceptionHandler(value = {
            CustomerNotFoundException.class,
            UserNotFoundException.class,
            SubscriptionNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ATDErrorResponse> handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ATDErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(value = {
            InvalidUserIdException.class,
            InvalidEventException.class,
            InvalidPromptException.class,
            InvalidModelTypeException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ATDErrorResponse> handleInvalidException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ATDErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(value = {
            InvalidSessionException.class,
            InvalidSubscriptionException.class,
            PromptLimitReachedException.class,
            TermsNotAcceptedException.class,
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ATDErrorResponse> handleInvalidSessionException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ATDErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler(value = ExistingUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ATDErrorResponse> handleExistingUsernameException(ExistingUsernameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ATDErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()));
    }
}
