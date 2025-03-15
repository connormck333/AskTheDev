package com.devconnor.askthedev.exception;

import java.util.UUID;

public class InvalidUserIdException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "[ATD] ERROR: Invalid user ID: ";

    public InvalidUserIdException(UUID userId) {
        super(EXCEPTION_MESSAGE + userId.toString());
    }
}
