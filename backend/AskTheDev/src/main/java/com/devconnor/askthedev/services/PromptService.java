package com.devconnor.askthedev.services;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.controllers.response.ATDResponse;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.devconnor.askthedev.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final OpenAIService openAIService;

    private final PromptRepository promptRepository;

    public ResponseEntity<ATDPromptResponse> sendPromptToOpenAI(Prompt prompt) {
        ATDPromptResponse atdPromptResponse = new ATDPromptResponse();
        if (!validatePromptRequest(atdPromptResponse, prompt) || !validateWebUrl(atdPromptResponse, prompt)) {
            return new ResponseEntity<>(atdPromptResponse, HttpStatus.BAD_REQUEST);
        }

        prompt.setOpenAIResponse(
                openAIService.sendPrompt(prompt.getPageContent(), prompt.getUserPrompt())
        );
        boolean validResponse = validatePromptResponse(atdPromptResponse, prompt.getOpenAIResponse());
        if (!validResponse) {
            return new ResponseEntity<>(atdPromptResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        atdPromptResponse.setPrompt(prompt);
        return ResponseEntity.ok(atdPromptResponse);
    }

    public ResponseEntity<ATDPromptListResponse> getPrompts(String webUrl, Long userId, int minPage) {
        ATDPromptListResponse atdPromptListResponse = new ATDPromptListResponse();
        webUrl = validateWebUrl(atdPromptListResponse, webUrl);
        if (webUrl == null) {
            atdPromptListResponse.setMessage(INVALID_WEB_URL_MESSAGE);
            return new ResponseEntity<>(atdPromptListResponse, HttpStatus.BAD_REQUEST);
        }

        List<Prompt> prompts = promptRepository.findLatestPrompts(webUrl, userId, 2, minPage);
        if (prompts == null) {
            atdPromptListResponse.setMessage(PROMPT_NOT_FOUND);
            return new ResponseEntity<>(atdPromptListResponse, HttpStatus.NOT_FOUND);
        }

        atdPromptListResponse.setPrompts(prompts);
        return ResponseEntity.ok(atdPromptListResponse);
    }

    private static boolean validatePromptRequest(ATDPromptResponse response, Prompt prompt) {
        if (prompt != null && prompt.getUserPrompt().length() >= MINIMUM_PROMPT_LENGTH) {
            return true;
        }

        response.setMessage(INVALID_PROMPT_MESSAGE);
        return false;
    }

    private static boolean validateWebUrl(ATDResponse atdResponse, Prompt prompt) {
        if (prompt.getWebUrl().isEmpty() || prompt.getWebUrl().length() < MINIMUM_URL_LENGTH) {
            atdResponse.setMessage(INVALID_WEB_URL_MESSAGE);
            return false;
        }

        String webUrl = validateWebUrl(atdResponse, prompt.getWebUrl());
        prompt.setWebUrl(webUrl);

        return true;
    }

    private static String validateWebUrl(ATDResponse atdResponse, String webUrl) {
        if (webUrl.length() < MINIMUM_URL_LENGTH) {
            atdResponse.setMessage(INVALID_WEB_URL_MESSAGE);
            return null;
        }

        if (webUrl.length() > MAXIMUM_URL_LENGTH) {
            webUrl = webUrl.substring(0, MAXIMUM_URL_LENGTH);
        }

        return webUrl;
    }

    private static boolean validatePromptResponse(ATDPromptResponse response, String openAIResponse) {
        if (openAIResponse == null || openAIResponse.isEmpty()) {
            response.setMessage(OPENAI_ERROR);
            return false;
        }

        return true;
    }
}
