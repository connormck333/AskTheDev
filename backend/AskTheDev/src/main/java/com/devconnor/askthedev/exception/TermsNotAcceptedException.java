package com.devconnor.askthedev.exception;

public class TermsNotAcceptedException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "You must accept the Terms & Conditions to create an account.";

    public TermsNotAcceptedException() {
        super(EXCEPTION_MESSAGE);
    }
}
