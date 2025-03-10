package com.devconnor.askthedev.controllers.response;

import com.devconnor.askthedev.models.Prompt;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ATDPromptListResponse extends ATDResponse {
    private List<Prompt> prompts;
}
