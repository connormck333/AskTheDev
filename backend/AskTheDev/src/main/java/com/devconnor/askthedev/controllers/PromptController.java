package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.controllers.response.ATDResponse;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.services.AuthenticationService;
import com.devconnor.askthedev.services.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ATDPromptResponse> prompt(@PathVariable Long userId, @RequestBody Prompt prompt) {
        ATDPromptResponse atdPromptResponse = (ATDPromptResponse) validateSession(userId);
        if (atdPromptResponse != null) {
            return new ResponseEntity<>(atdPromptResponse, HttpStatus.UNAUTHORIZED);
        }

        prompt.setUserId(userId);

        ResponseEntity<ATDPromptResponse> response = promptService.sendPromptToOpenAI(prompt);
        savePrompt(prompt);

        return response;
    }

    @GetMapping("/retrieve")
    public ResponseEntity<ATDPromptListResponse> retrievePrompts(
            @RequestParam Long id,
            @RequestParam String webUrl,
            @RequestParam int minPage
    ) {
        ATDPromptListResponse atdPromptResponse = (ATDPromptListResponse) validateSession(id);
        if (atdPromptResponse != null) {
            return new ResponseEntity<>(atdPromptResponse, HttpStatus.UNAUTHORIZED);
        }

        return promptService.getPrompts(webUrl, id, minPage);
    }

    private ATDResponse validateSession(Long userId) {
        ATDResponse atdPromptResponse = new ATDResponse();
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
