package com.devconnor.askthedev.exception;

public class SubscriptionNotFoundException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Subscription not found.";

    public SubscriptionNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }
}
