package com.devconnor.askthedev.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Constants {
    public static final int MINIMUM_PROMPT_LENGTH = 5;
    public static final int MINIMUM_URL_LENGTH = 5;
    public static final int MAXIMUM_URL_LENGTH = 255;

    public static final String INVALID_SESSION_MESSAGE = "Invalid Session";
    public static final String INVALID_PROMPT_MESSAGE = "Invalid Prompt: Please send a valid prompt.";
    public static final String INVALID_WEB_URL_MESSAGE = "Invalid Web URL: Please enter a valid URL.";
    public static final String PROMPT_NOT_FOUND = "Prompt Not Found";
    public static final String USER_NOT_FOUND = "User Not Found";

    public static final long AUTH_EXPIRATION_TIME = (long) 1000 * 60 * 60 * 24 * 7;

    public static final List<Character> SPECIAL_CHARACTERS = List.of('!', '@', '£', '#', '$', '%', '^', '&', '*', '-');
}
