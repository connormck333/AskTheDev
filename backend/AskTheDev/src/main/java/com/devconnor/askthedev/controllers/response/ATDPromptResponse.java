package com.devconnor.askthedev.controllers.response;

import com.devconnor.askthedev.models.Prompt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATDPromptResponse {
    private Prompt prompt;
    private String message;
}
