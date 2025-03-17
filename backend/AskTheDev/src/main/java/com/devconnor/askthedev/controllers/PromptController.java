package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.controllers.response.ATDResponse;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.services.prompt.PromptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Constants.INACTIVE_SUBSCRIPTION_MESSAGE;
import static com.devconnor.askthedev.utils.Constants.INVALID_SESSION_MESSAGE;

@RestController
@RequestMapping("/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final JwtUtil jwtUtil;
    private final PromptService promptService;
    private final PromptRepository promptRepository;
    private final SubscriptionRepository subscriptionRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<ATDPromptResponse> prompt(
            HttpServletRequest request,
            @PathVariable UUID userId,
            @RequestBody Prompt prompt
    ) {
        ATDResponse atdPromptResponse = validateSession(request, userId);
        if (atdPromptResponse != null) {
            ATDPromptResponse response = new ATDPromptResponse();
            response.setMessage(atdPromptResponse.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        prompt.setUserId(userId);

        ResponseEntity<ATDPromptResponse> response = promptService.sendPromptToOpenAI(prompt);
        savePrompt(prompt);

        return response;
    }

    @GetMapping("/retrieve")
    public ResponseEntity<ATDPromptListResponse> retrievePrompts(
            HttpServletRequest request,
            @RequestParam UUID id,
            @RequestParam String webUrl,
            @RequestParam int minPage
    ) {
        ATDPromptListResponse atdPromptResponse = (ATDPromptListResponse) validateSession(request, id);
        if (atdPromptResponse != null) {
            return new ResponseEntity<>(atdPromptResponse, HttpStatus.UNAUTHORIZED);
        }

        return promptService.getPrompts(webUrl, id, minPage);
    }

    private ATDResponse validateSession(HttpServletRequest request, UUID userId) {
        if (!jwtUtil.isSessionValid(request, userId)) {
            ATDResponse atdPromptResponse = new ATDResponse();
            atdPromptResponse.setMessage(INVALID_SESSION_MESSAGE);
            return atdPromptResponse;
        }

        return validateSubscription(userId);
    }

    private ATDResponse validateSubscription(UUID userId) {
        ATDSubscription subscription = subscriptionRepository.getSubscriptionByUserId(userId);
        if (subscription == null || !subscription.isActive()) {
            ATDResponse atdPromptResponse = new ATDResponse();
            atdPromptResponse.setMessage(INACTIVE_SUBSCRIPTION_MESSAGE);
            return atdPromptResponse;
        }

        return null;
    }

    private void savePrompt(Prompt prompt) {
        promptRepository.save(prompt);
    }
}
