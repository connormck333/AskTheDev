package com.devconnor.askthedev.services.prompt;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.exception.InvalidPromptException;
import com.devconnor.askthedev.exception.PromptLimitReachedException;
import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.devconnor.askthedev.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Constants.MAXIMUM_URL_LENGTH;
import static com.devconnor.askthedev.utils.Constants.PROMPT_NOT_FOUND;
import static com.devconnor.askthedev.utils.Utils.createATDSubscription;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptServiceTest {

    private static final String PROMPT_TEXT = "promptText";
    private static final String WEB_URL = "webUrl";
    private static final String PAGE_CONTENT = "pageContent";

    private PromptService promptService;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private PromptRepository promptRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        promptService = new PromptService(openAIService, promptRepository, subscriptionRepository);
    }

    @Test
    void testSendPromptToOpenAI_Successful_WithNoPreviousPromptsToday() {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        ATDSubscription subscription = createATDSubscription(userId);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(subscription);
        when(promptRepository.findAllByUserIdAndCreatedAtToday(eq(userId), any(LocalDateTime.class))).thenReturn(List.of());

        assertDoesNotThrow(() -> promptService.sendPromptToOpenAI(prompt));

        verify(openAIService, times(1)).sendPrompt(anyString(), anyString());
    }

    @Test
    void testSendPromptToOpenAI_Successful_WithBelowLimitPromptsToday() {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        List<Prompt> previousTodayPrompts = createPromptList(5, userId);
        ATDSubscription subscription = createATDSubscription(userId);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(subscription);
        when(promptRepository.findAllByUserIdAndCreatedAtToday(eq(userId), any(LocalDateTime.class))).thenReturn(previousTodayPrompts);

        assertDoesNotThrow(() -> promptService.sendPromptToOpenAI(prompt));

        verify(openAIService, times(1)).sendPrompt(anyString(), anyString());
    }

    @ParameterizedTest
    @EnumSource(SubscriptionType.class)
    void testSendPromptToOpenAI_PromptLimitReached(SubscriptionType subscriptionType) {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        List<Prompt> previousTodayPrompts = createPromptList(50, userId);
        ATDSubscription subscription = createATDSubscription(userId);
        subscription.setType(subscriptionType);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(subscription);
        when(promptRepository.findAllByUserIdAndCreatedAtToday(eq(userId), any(LocalDateTime.class))).thenReturn(previousTodayPrompts);

        assertThrows(PromptLimitReachedException.class, () -> promptService.sendPromptToOpenAI(prompt));
    }

    @Test
    void testSendPromptToOpenAI_SubscriptionNull() {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(null);

        assertThrows(SubscriptionNotFoundException.class, () -> promptService.sendPromptToOpenAI(prompt));
    }

    @Test
    void testSendPromptToOpenAI_SubscriptionInactive() {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        ATDSubscription subscription = createATDSubscription(userId);
        subscription.setActive(false);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(null);

        assertThrows(SubscriptionNotFoundException.class, () -> promptService.sendPromptToOpenAI(prompt));
    }

    @Test
    void testSendPromptToOpenAI_TodayPromptsNull() {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        ATDSubscription subscription = createATDSubscription(userId);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(subscription);
        when(promptRepository.findAllByUserIdAndCreatedAtToday(eq(userId), any(LocalDateTime.class))).thenReturn(null);

        assertDoesNotThrow(() -> promptService.sendPromptToOpenAI(prompt));

        verify(openAIService, times(1)).sendPrompt(anyString(), anyString());
    }

    @Test
    void testSendPromptToOpenAI_NullPrompt() {
        assertThrows(InvalidPromptException.class, () -> promptService.sendPromptToOpenAI(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234"})
    @NullAndEmptySource
    void testSendPromptToOpenAI_InvalidPromptText(String promptText) {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        prompt.setUserPrompt(promptText);

        assertThrows(InvalidPromptException.class, () -> promptService.sendPromptToOpenAI(prompt));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234"})
    @NullAndEmptySource
    void testSendPromptToOpenAI_InvalidWebUrl(String webUrl) {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        prompt.setWebUrl(webUrl);

        assertThrows(InvalidPromptException.class, () -> promptService.sendPromptToOpenAI(prompt));
    }

    @Test
    void testSendPromptToOpenAI_OpenAIThrowsException() {
        UUID userId = UUID.randomUUID();
        Prompt prompt = createPrompt(userId);
        ATDSubscription subscription = createATDSubscription(userId);

        when(subscriptionRepository.getSubscriptionByUserId(userId)).thenReturn(subscription);
        when(promptRepository.findAllByUserIdAndCreatedAtToday(eq(userId), any(LocalDateTime.class))).thenReturn(List.of());
        when(openAIService.sendPrompt(anyString(), anyString())).thenThrow(InvalidPromptException.class);

        assertThrows(InvalidPromptException.class, () -> promptService.sendPromptToOpenAI(prompt));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void testGetPrompts_Successful_OnePrompt(int amount) {
        UUID userId = UUID.randomUUID();
        List<Prompt> prompts = createPromptList(amount, userId);

        when(promptRepository.findLatestPrompts(eq(WEB_URL), eq(userId), anyInt(), anyInt())).thenReturn(prompts);

        ATDPromptListResponse response = promptService.getPrompts(WEB_URL, userId, 0);

        for (Prompt responsePrompt : response.getPrompts()) {
            assertTrue(prompts.contains(responsePrompt));
        }
    }

    @Test
    void testGetPrompts_Successful_NoExistingPrompts() {
        UUID userId = UUID.randomUUID();

        when(promptRepository.findLatestPrompts(eq(WEB_URL), eq(userId), anyInt(), anyInt())).thenReturn(null);

        ATDPromptListResponse response = promptService.getPrompts(WEB_URL, userId, 0);

        assertNull(response.getPrompts());
        assertEquals(PROMPT_NOT_FOUND, response.getMessage());
    }

    @Test
    void testGetPrompts_Successful_EmptyPrompts() {
        UUID userId = UUID.randomUUID();
        List<Prompt> prompts = new ArrayList<>();

        when(promptRepository.findLatestPrompts(eq(WEB_URL), eq(userId), anyInt(), anyInt())).thenReturn(prompts);

        ATDPromptListResponse response = promptService.getPrompts(WEB_URL, userId, 0);

        assertTrue(response.getPrompts().isEmpty());
        assertNull(response.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234"})
    @NullAndEmptySource
    void testGetPrompts_InvalidWebUrl(String webUrl) {
        UUID userId = UUID.randomUUID();

        assertThrows(InvalidPromptException.class, () -> promptService.getPrompts(webUrl, userId, 0));
    }

    @Test
    void testGetPrompts_ShouldTrimWebUrl() {
        UUID userId = UUID.randomUUID();
        List<Prompt> prompts = createPromptList(1, userId);
        String longWebUrl = Utils.generateRandomString(MAXIMUM_URL_LENGTH + 10);

        when(promptRepository.findLatestPrompts(eq(longWebUrl.substring(0, MAXIMUM_URL_LENGTH)), eq(userId), anyInt(), anyInt()))
                .thenReturn(prompts);

        ATDPromptListResponse response = promptService.getPrompts(longWebUrl, userId, 0);

        assertEquals(1, response.getPrompts().size());
    }

    private static Prompt createPrompt(UUID userId) {
        Prompt prompt = new Prompt();
        prompt.setWebUrl(WEB_URL);
        prompt.setUserPrompt(PROMPT_TEXT);
        prompt.setUserId(userId);
        prompt.setPageContent(PAGE_CONTENT);

        return prompt;
    }

    private static List<Prompt> createPromptList(int amount, UUID userId) {
        List<Prompt> prompts = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            prompts.add(createPrompt(userId));
        }

        return prompts;
    }
}
