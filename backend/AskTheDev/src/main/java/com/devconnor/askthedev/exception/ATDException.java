package com.devconnor.askthedev.exception;

public class ATDException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "[ATD] ERROR: ";
    private static final String DEFAULT_MESSAGE = "An unknown error occurred.";

    public ATDException() {
        super(EXCEPTION_MESSAGE + DEFAULT_MESSAGE);
    }

    public ATDException(String message) {
        super(EXCEPTION_MESSAGE + (message == null ? DEFAULT_MESSAGE : message));
    }
}
