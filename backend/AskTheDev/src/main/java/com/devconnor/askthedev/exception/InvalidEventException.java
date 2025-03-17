package com.devconnor.askthedev.exception;

public class InvalidEventException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Invalid event.";

    public InvalidEventException() {
        super(EXCEPTION_MESSAGE);
    }
}
