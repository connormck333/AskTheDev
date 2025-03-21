package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.controllers.response.ATDResponse;
import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.services.prompt.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Constants.INACTIVE_SUBSCRIPTION_MESSAGE;

@RestController
@RequestMapping("/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;
    private final PromptRepository promptRepository;
    private final SubscriptionRepository subscriptionRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<ATDPromptResponse> prompt(
            @PathVariable UUID userId,
            @RequestBody Prompt prompt
    ) {
        try {
            validateSubscription(userId);
        } catch (Exception e) {
            ATDPromptResponse response = new ATDPromptResponse();
            response.setMessage(INACTIVE_SUBSCRIPTION_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        prompt.setUserId(userId);

        ATDPromptResponse response = promptService.sendPromptToOpenAI(prompt);
        savePrompt(prompt);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieve")
    public ResponseEntity<ATDPromptListResponse> retrievePrompts(
            @RequestParam UUID id,
            @RequestParam String webUrl,
            @RequestParam int minPage
    ) {
        try {
            validateSubscription(id);
        } catch (Exception e) {
            ATDPromptListResponse response = new ATDPromptListResponse();
            response.setMessage(INACTIVE_SUBSCRIPTION_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ATDPromptListResponse response = promptService.getPrompts(webUrl, id, minPage);

        return ResponseEntity.ok(response);
    }

    private void validateSubscription(UUID userId) {
        ATDSubscription subscription = subscriptionRepository.getSubscriptionByUserId(userId);
        if (subscription == null || !subscription.isActive()) {
            throw new SubscriptionNotFoundException();
        }
    }

    private void savePrompt(Prompt prompt) {
        promptRepository.save(prompt);
    }
}
