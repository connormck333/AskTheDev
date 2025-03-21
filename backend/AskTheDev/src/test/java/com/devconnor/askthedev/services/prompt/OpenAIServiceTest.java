package com.devconnor.askthedev.services.prompt;

import com.devconnor.askthedev.exception.InvalidPromptException;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionMessage;
import com.openai.services.blocking.ChatService;
import com.openai.services.blocking.chat.CompletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAIServiceTest {

    private static final String PROMPT_TEXT = "promptText";
    private static final String PAGE_CONTENT = "pageContent";
    private static final String RESPONSE_TEXT = "responseText";

    private OpenAIService openAIService;

    @Mock
    private OpenAIClient openAIClient;

    @Mock
    private ChatCompletion chatCompletion;

    @Mock
    private ChatService chatService;

    @Mock
    private CompletionService completionService;

    @BeforeEach
    void setUp() {
        openAIService = new OpenAIService(openAIClient);
    }

    @Test
    void testSendPrompt_Successful() {
        List<ChatCompletion.Choice> choices = createChoices();

        when(openAIClient.chat()).thenReturn(chatService);
        when(chatService.completions()).thenReturn(completionService);
        when(completionService.create(any(ChatCompletionCreateParams.class))).thenReturn(chatCompletion);
        when(chatCompletion.choices()).thenReturn(choices);

        String response = openAIService.sendPrompt(PAGE_CONTENT, PROMPT_TEXT);

        assertEquals(RESPONSE_TEXT, response);
    }

    @Test
    void testSendPrompt_InvalidPrompt() {
        when(openAIClient.chat()).thenReturn(chatService);
        when(chatService.completions()).thenReturn(completionService);
        when(completionService.create(any(ChatCompletionCreateParams.class))).thenThrow(InvalidPromptException.class);

        assertThrows(InvalidPromptException.class, () -> openAIService.sendPrompt(PAGE_CONTENT, PROMPT_TEXT));
    }

    @Test
    void testSendPrompt_NullChoices() {
        when(openAIClient.chat()).thenReturn(chatService);
        when(chatService.completions()).thenReturn(completionService);
        when(completionService.create(any(ChatCompletionCreateParams.class))).thenReturn(chatCompletion);
        when(chatCompletion.choices()).thenReturn(null);

        assertThrows(InvalidPromptException.class, () -> openAIService.sendPrompt(PAGE_CONTENT, PROMPT_TEXT));
    }

    @Test
    void testSendPrompt_EmptyChoices() {
        List<ChatCompletion.Choice> choices = new ArrayList<>();

        when(openAIClient.chat()).thenReturn(chatService);
        when(chatService.completions()).thenReturn(completionService);
        when(completionService.create(any(ChatCompletionCreateParams.class))).thenReturn(chatCompletion);
        when(chatCompletion.choices()).thenReturn(choices);

        assertThrows(InvalidPromptException.class, () -> openAIService.sendPrompt(PAGE_CONTENT, PROMPT_TEXT));
    }

    @Test
    void testSendPrompt_EmptyContent() {
        List<ChatCompletion.Choice> choices = createChoices(null);

        when(openAIClient.chat()).thenReturn(chatService);
        when(chatService.completions()).thenReturn(completionService);
        when(completionService.create(any(ChatCompletionCreateParams.class))).thenReturn(chatCompletion);
        when(chatCompletion.choices()).thenReturn(choices);

        assertThrows(InvalidPromptException.class, () -> openAIService.sendPrompt(PAGE_CONTENT, PROMPT_TEXT));
    }

    private List<ChatCompletion.Choice> createChoices() {
        return createChoices(RESPONSE_TEXT);
    }

    private List<ChatCompletion.Choice> createChoices(String content) {
        List<ChatCompletion.Choice> choices = new ArrayList<>();

        ChatCompletionMessage message = ChatCompletionMessage.builder()
                .content(content)
                .refusal("refusal")
                .build();

        ChatCompletion.Choice.FinishReason finishReason = ChatCompletion.Choice.FinishReason.LENGTH;

        ChatCompletion.Choice choice = ChatCompletion.Choice.builder()
                .message(message)
                .finishReason(finishReason)
                .index(0L)
                .logprobs(Optional.empty())
                .build();

        choices.add(choice);

        return choices;
    }
}
