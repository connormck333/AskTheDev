package com.devconnor.askthedev.exception;

public class CustomerNotFoundException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Customer not found.";

    public CustomerNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }
}
