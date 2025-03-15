package com.devconnor.askthedev.exception;

public class ATDException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "[ATD] ERROR: An error occurred.";

    public ATDException() {
        super(EXCEPTION_MESSAGE);
    }
}
