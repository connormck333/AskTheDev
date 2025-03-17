package com.devconnor.askthedev.exception;

public class UserNotFoundException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "User with email %s not found.";

    public UserNotFoundException(String email) {
        super(String.format(EXCEPTION_MESSAGE, email));
    }
}
