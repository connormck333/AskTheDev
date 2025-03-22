package com.devconnor.askthedev.exception.handlers;

import com.devconnor.askthedev.exception.*;
import com.devconnor.askthedev.models.ATDErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ATDExceptionHandler {

    private static final String PREFIX = "[ATD] Error: %s";

    @ExceptionHandler(value = ATDException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ATDErrorResponse handleATDException(ATDException e) {
        return new ATDErrorResponse(HttpStatus.BAD_REQUEST.value(), String.format(PREFIX, "An error occurred."));
    }

    @ExceptionHandler(value = {
            CustomerNotFoundException.class,
            UserNotFoundException.class,
            SubscriptionNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ATDErrorResponse handleNotFoundException(Exception e) {
        return new ATDErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = ExistingUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ATDErrorResponse handleExistingUsernameException(ExistingUsernameException e) {
        return new ATDErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = {
            InvalidUserIdException.class,
            InvalidEventException.class,
            InvalidPromptException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ATDErrorResponse handleInvalidException(Exception e) {
        return new ATDErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = {
            InvalidSessionException.class,
            InvalidSubscriptionException.class,
            PromptLimitReachedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ATDErrorResponse handleInvalidSessionException(Exception e) {
        return new ATDErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }
}
