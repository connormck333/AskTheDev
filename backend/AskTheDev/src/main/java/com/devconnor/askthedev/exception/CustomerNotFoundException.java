package com.devconnor.askthedev.exception;

public class CustomerNotFoundException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "[ATD] ERROR: Customer not found.";

    public CustomerNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }
}
