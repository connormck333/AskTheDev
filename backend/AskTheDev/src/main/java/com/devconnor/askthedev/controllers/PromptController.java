package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.services.AuthenticationService;
import com.devconnor.askthedev.services.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.devconnor.askthedev.utils.Constants.INVALID_SESSION_MESSAGE;

@RestController
@RequestMapping("/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final AuthenticationService authenticationService;

    private final PromptService promptService;

    private final PromptRepository promptRepository;

    @PostMapping("/{userId}")
    public ATDPromptResponse prompt(@PathVariable Long userId, @RequestBody Prompt prompt) {
        ATDPromptResponse atdPromptResponse = validateSession(userId);
        if (atdPromptResponse != null) {
            return atdPromptResponse;
        }

        prompt.setUserId(userId);

        atdPromptResponse = promptService.sendPromptToOpenAI(prompt);
        savePrompt(prompt);

        return atdPromptResponse;
    }

    private ATDPromptResponse validateSession(Long userId) {
        ATDPromptResponse atdPromptResponse = new ATDPromptResponse();
        if (!authenticationService.verifyUserSession(userId)) {
            atdPromptResponse.setMessage(INVALID_SESSION_MESSAGE);
            return atdPromptResponse;
        }

        return null;
    }

    private void savePrompt(Prompt prompt) {
        promptRepository.save(prompt);
    }
}
