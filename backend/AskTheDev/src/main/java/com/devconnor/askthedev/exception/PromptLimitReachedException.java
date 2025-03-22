package com.devconnor.askthedev.exception;

public class PromptLimitReachedException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Prompt limit reached.";

    public PromptLimitReachedException() {
        super(EXCEPTION_MESSAGE);
    }
}
