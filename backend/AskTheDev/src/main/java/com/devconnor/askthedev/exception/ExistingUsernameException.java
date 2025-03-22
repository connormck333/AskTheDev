package com.devconnor.askthedev.exception;

public class ExistingUsernameException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "A user with this email already exists.";

    public ExistingUsernameException() {
        super(EXCEPTION_MESSAGE);
    }
}
