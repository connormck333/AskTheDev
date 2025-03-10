package com.devconnor.askthedev.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final int MINIMUM_PROMPT_LENGTH = 5;

    public static final String INVALID_SESSION_MESSAGE = "Invalid Session";
    public static final String INVALID_PROMPT_MESSAGE = "Invalid Prompt: Please send a valid prompt.";
    public static final String OPENAI_ERROR = "OpenAI Error: There was an error processing your request.";
}
