package com.devconnor.askthedev.services.prompt;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.controllers.response.ATDResponse;
import com.devconnor.askthedev.exception.*;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.utils.ModelType;
import com.devconnor.askthedev.utils.SubscriptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final OpenAIService openAIService;
    private final PromptRepository promptRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ATDPromptResponse sendPromptToOpenAI(Prompt prompt) {
        ATDPromptResponse atdPromptResponse = new ATDPromptResponse();

        if (!isValidPromptRequest(atdPromptResponse, prompt) || !isValidWebUrl(atdPromptResponse, prompt.getWebUrl())) {
            throw new InvalidPromptException();
        }
        validateUserSubscription(prompt.getUserId(), prompt.getModelType(), false);

        prompt.setOpenAIResponse(
                openAIService.sendPrompt(prompt.getPageContent(), prompt.getUserPrompt(), prompt.getModelType())
        );

        atdPromptResponse.setPrompt(prompt);
        return atdPromptResponse;
    }

    public ATDPromptResponse summariseWebPage(Prompt prompt) {
        ATDPromptResponse atdPromptResponse = new ATDPromptResponse();
        if (!isValidWebUrl(atdPromptResponse, prompt.getWebUrl())) {
            throw new InvalidPromptException();
        }

        validateUserSubscription(prompt.getUserId(), prompt.getModelType(), true);
        prompt.setUserPrompt("Summarise this web page.");

        prompt.setOpenAIResponse(
                openAIService.summariseWebPage(prompt.getPageContent(), prompt.getModelType())
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

    private void validateUserSubscription(UUID userId, ModelType modelType, boolean expectedPro) {
        ATDSubscription subscription = subscriptionRepository.getSubscriptionByUserId(userId);
        if (
                subscription == null
                || !subscription.isActive()
        ) {
            throw new SubscriptionNotFoundException();
        }

        if (expectedPro && subscription.getType() != SubscriptionType.PRO) {
            throw new InvalidSubscriptionException();
        }

        if (modelType != ModelType.GPT4O_MINI && subscription.getType() != SubscriptionType.PRO) {
            throw new InvalidModelTypeException(modelType.getValue());
        }

        validateUserPromptsAmount(userId, subscription.getType());
    }

    private void validateUserPromptsAmount(UUID userId, SubscriptionType subscriptionType) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        List<Prompt> todayPrompts = promptRepository.findAllByUserIdAndCreatedAtToday(userId, startOfDay);

        int permittedAmount = subscriptionType.getPromptAmount();
        if (todayPrompts != null && todayPrompts.size() >= permittedAmount) {
            throw new PromptLimitReachedException();
        }
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
