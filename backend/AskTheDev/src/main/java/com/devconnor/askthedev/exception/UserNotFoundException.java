package com.devconnor.askthedev.exception;

public class UserNotFoundException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "User not found.";
    private static final String EXCEPTION_MESSAGE_EMAIL = "User with email %s not found.";

    public UserNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }

    public UserNotFoundException(String email) {
        super(String.format(EXCEPTION_MESSAGE_EMAIL, email));
    }
}
