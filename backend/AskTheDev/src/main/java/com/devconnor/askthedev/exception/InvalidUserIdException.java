package com.devconnor.askthedev.exception;

import java.util.UUID;

public class InvalidUserIdException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Invalid user ID: %s";

    public InvalidUserIdException(UUID userId) {
        super(String.format(EXCEPTION_MESSAGE, userId.toString()));
    }
}
