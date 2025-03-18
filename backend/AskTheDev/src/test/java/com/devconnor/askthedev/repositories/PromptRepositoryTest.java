package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.Prompt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PromptRepositoryTest {

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final String WEB_URL = "http://localhost:8080";

    @Test
    void testFindLatestPrompts_WithOffset0() {
        UUID userId = UUID.randomUUID();
        int promptAmount = 3;

        List<Prompt> prompts = createPromptList(promptAmount, userId);
        for (Prompt prompt : prompts) {
            entityManager.persist(prompt);
        }
        entityManager.flush();

        List<Prompt> foundPrompts = promptRepository.findLatestPrompts(WEB_URL, userId, 5, 0);

        assertEquals(promptAmount, foundPrompts.size());
        for (int i = 0; i < foundPrompts.size(); i++) {
            Prompt foundPrompt = foundPrompts.get(i);
            Prompt originalPrompt = prompts.get(i);

            assertEquals(userId, foundPrompt.getUserId());
            assertEquals(originalPrompt.getUserPrompt(), foundPrompt.getUserPrompt());
            assertEquals(originalPrompt.getWebUrl(), foundPrompt.getWebUrl());
            assertEquals(originalPrompt.getPageContent(), foundPrompt.getPageContent());
            assertEquals(originalPrompt.getOpenAIResponse(), foundPrompt.getOpenAIResponse());
            assertNotNull(foundPrompt.getId());
            assertNotNull(foundPrompt.getCreatedAt());
        }
    }

    @Test
    void testFindLatestPrompts_WithOffset3() {
        UUID userId = UUID.randomUUID();
        int promptAmount = 10;
        int offset = 3;
        int limit = 5;

        List<Prompt> prompts = createPromptList(promptAmount, userId);
        for (Prompt prompt : prompts) {
            entityManager.persist(prompt);
        }
        entityManager.flush();

        List<Prompt> foundPrompts = promptRepository.findLatestPrompts(WEB_URL, userId, limit, offset);

        assertEquals(5, foundPrompts.size());
        for (int i = 0; i < foundPrompts.size(); i++) {
            Prompt foundPrompt = foundPrompts.get(i);
            Prompt originalPrompt = prompts.get(i + offset);

            assertEquals(userId, foundPrompt.getUserId());
            assertEquals(originalPrompt.getUserPrompt(), foundPrompt.getUserPrompt());
            assertEquals(originalPrompt.getWebUrl(), foundPrompt.getWebUrl());
            assertEquals(originalPrompt.getPageContent(), foundPrompt.getPageContent());
            assertEquals(originalPrompt.getOpenAIResponse(), foundPrompt.getOpenAIResponse());
            assertNotNull(foundPrompt.getId());
            assertNotNull(foundPrompt.getCreatedAt());
        }
    }

    private static List<Prompt> createPromptList(int amount, UUID userId) {
        List<Prompt> prompts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Prompt prompt = new Prompt();
            prompt.setUserPrompt("user prompt");
            prompt.setWebUrl(WEB_URL);
            prompt.setOpenAIResponse("openAI response");
            prompt.setPageContent("page content");
            prompt.setUserId(userId);
            prompts.add(prompt);
        }

        return prompts;
    }
}
