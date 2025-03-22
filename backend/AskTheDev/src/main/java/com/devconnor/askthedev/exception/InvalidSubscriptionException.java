package com.devconnor.askthedev.exception;

public class InvalidSubscriptionException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Invalid subscription.";

    public InvalidSubscriptionException() {
        super(EXCEPTION_MESSAGE);
    }
}
