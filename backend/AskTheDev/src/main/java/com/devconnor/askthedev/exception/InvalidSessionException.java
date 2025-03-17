package com.devconnor.askthedev.exception;

public class InvalidSessionException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Invalid session.";

    public InvalidSessionException() {
        super(EXCEPTION_MESSAGE);
    }
}
