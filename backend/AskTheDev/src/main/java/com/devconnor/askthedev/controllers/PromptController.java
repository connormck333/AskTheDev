package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.services.prompt.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;
    private final PromptRepository promptRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<ATDPromptResponse> prompt(
            @PathVariable UUID userId,
            @RequestBody Prompt prompt
    ) {
        prompt.setUserId(userId);

        ATDPromptResponse response = promptService.sendPromptToOpenAI(prompt);
        savePrompt(prompt);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/summarise/{userId}")
    public ResponseEntity<ATDPromptResponse> summarise(
            @PathVariable UUID userId,
            @RequestBody Prompt prompt
    ) {
        prompt.setUserId(userId);

        ATDPromptResponse response = promptService.summariseWebPage(prompt);
        savePrompt(prompt);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieve")
    public ResponseEntity<ATDPromptListResponse> retrievePrompts(
            @RequestParam UUID id,
            @RequestParam String webUrl,
            @RequestParam int minPage
    ) {
        ATDPromptListResponse response = promptService.getPrompts(webUrl, id, minPage);

        return ResponseEntity.ok(response);
    }

    private void savePrompt(Prompt prompt) {
        promptRepository.save(prompt);
    }
}
