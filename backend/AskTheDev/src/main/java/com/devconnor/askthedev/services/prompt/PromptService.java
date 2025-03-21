package com.devconnor.askthedev.services.prompt;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.controllers.response.ATDResponse;
import com.devconnor.askthedev.exception.InvalidPromptException;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final OpenAIService openAIService;

    private final PromptRepository promptRepository;

    public ATDPromptResponse sendPromptToOpenAI(Prompt prompt) {
        ATDPromptResponse atdPromptResponse = new ATDPromptResponse();
        if (!isValidPromptRequest(atdPromptResponse, prompt) || !isValidWebUrl(atdPromptResponse, prompt.getWebUrl())) {
            throw new InvalidPromptException();
        }

        prompt.setOpenAIResponse(
                openAIService.sendPrompt(prompt.getPageContent(), prompt.getUserPrompt())
        );

        atdPromptResponse.setPrompt(prompt);
        return atdPromptResponse;
    }

    public ATDPromptListResponse getPrompts(String webUrl, UUID userId, int minPage) {
        ATDPromptListResponse atdPromptListResponse = new ATDPromptListResponse();
        webUrl = isValidWebUrlLength(atdPromptListResponse, webUrl);

        List<Prompt> prompts = promptRepository.findLatestPrompts(webUrl, userId, 5, minPage);
        if (prompts == null) {
            atdPromptListResponse.setMessage(PROMPT_NOT_FOUND);
            return atdPromptListResponse;
        }

        atdPromptListResponse.setPrompts(prompts);
        return atdPromptListResponse;
    }

    private static boolean isValidPromptRequest(ATDPromptResponse response, Prompt prompt) {
        if (
                prompt == null
                || prompt.getUserPrompt() == null
                || prompt.getUserPrompt().length() < MINIMUM_PROMPT_LENGTH
        ) {
            response.setMessage(INVALID_PROMPT_MESSAGE);
            return false;
        }

        return true;
    }

    private static boolean isValidWebUrl(ATDResponse atdResponse, String webUrl) {
        if (webUrl == null || webUrl.length() < MINIMUM_URL_LENGTH) {
            atdResponse.setMessage(INVALID_WEB_URL_MESSAGE);
            return false;
        }

        return true;
    }

    private static String isValidWebUrlLength(ATDResponse atdResponse, String webUrl) {
        if (!isValidWebUrl(atdResponse, webUrl)) {
            throw new InvalidPromptException();
        }

        if (webUrl.length() > MAXIMUM_URL_LENGTH) {
            webUrl = webUrl.substring(0, MAXIMUM_URL_LENGTH);
        }

        return webUrl;
    }
}
