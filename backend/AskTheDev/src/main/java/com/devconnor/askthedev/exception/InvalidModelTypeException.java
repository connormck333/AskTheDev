package com.devconnor.askthedev.exception;

public class InvalidModelTypeException extends ATDException {

    private static final String EXCEPTION_MESSAGE = "Invalid model type: %s";

    public InvalidModelTypeException(String modelType) {
        super(String.format(EXCEPTION_MESSAGE, modelType));
    }
}
