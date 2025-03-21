package com.devconnor.askthedev.exception;

public class InvalidPromptException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Invalid prompt.";

    public InvalidPromptException() {
        super(EXCEPTION_MESSAGE);
    }
}
