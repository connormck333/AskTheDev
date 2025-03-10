package com.devconnor.askthedev.services;

import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.models.Prompt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.devconnor.askthedev.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final OpenAIService openAIService;

    public ATDPromptResponse sendPromptToOpenAI(Prompt prompt) {
        ATDPromptResponse atdPromptResponse = new ATDPromptResponse();
        if (!validatePromptRequest(atdPromptResponse, prompt)) {
            return atdPromptResponse;
        }

        prompt.setOpenAIResponse(
                openAIService.sendPrompt(prompt.getPageContent(), prompt.getUserPrompt())
        );
        validatePromptResponse(atdPromptResponse, prompt.getOpenAIResponse());

        atdPromptResponse.setPrompt(prompt);

        return atdPromptResponse;
    }

    private static boolean validatePromptRequest(ATDPromptResponse response, Prompt prompt) {
        if (prompt != null && prompt.getUserPrompt().length() >= MINIMUM_PROMPT_LENGTH) {
            return true;
        }

        response.setMessage(INVALID_PROMPT_MESSAGE);
        return false;
    }

    private static void validatePromptResponse(ATDPromptResponse response, String openAIResponse) {
        if (openAIResponse == null || openAIResponse.isEmpty()) {
            response.setMessage(OPENAI_ERROR);
        }
    }
}
