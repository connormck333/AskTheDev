package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDPromptListResponse;
import com.devconnor.askthedev.controllers.response.ATDPromptResponse;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.Prompt;
import com.devconnor.askthedev.repositories.PromptRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.security.SecurityConfig;
import com.devconnor.askthedev.services.prompt.PromptService;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.APPLICATION_JSON;
import static com.devconnor.askthedev.utils.Utils.convertToJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PromptController.class)
@Import({SecurityConfig.class})
@NoArgsConstructor
class PromptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromptService promptService;

    @MockitoBean
    private PromptRepository promptRepository;

    @MockitoBean
    private SubscriptionRepository subscriptionRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    private static final String USER_ID_PARAM = "id";
    private static final String WEB_URL_PARAM = "webUrl";
    private static final String MIN_PAGE_PARAM = "minPage";

    private static final String WEB_URL = "https://www.devconnor.com/";

    @Test
    @WithMockUser
    void testPrompt_Successful() throws Exception {
        UUID userId = UUID.randomUUID();
        Prompt prompt = generatePrompt(userId);
        ATDPromptResponse promptResponse = generatePromptResponse(prompt);

        when(promptService.sendPromptToOpenAI(any(Prompt.class))).thenReturn(promptResponse);

        String body = convertToJson(prompt);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/prompt/%s", userId))
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.userPrompt").value(prompt.getUserPrompt()))
                .andExpect(jsonPath("$.prompt.pageContent").value(prompt.getPageContent()))
                .andExpect(jsonPath("$.prompt.webUrl").value(prompt.getWebUrl()))
                .andExpect(jsonPath("$.prompt.id").exists())
                .andExpect(jsonPath("$.prompt.openAIResponse").exists())
                .andExpect(jsonPath("$.prompt.createdAt").exists())
                .andExpect(jsonPath("$.prompt.userId").value(userId.toString()));
    }

    @Test
    void testPrompt_NotLoggedIn() throws Exception {
        UUID userId = UUID.randomUUID();
        Prompt prompt = generatePrompt(userId);

        String body = convertToJson(prompt);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/prompt/%s", userId))
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testPrompt_EmptyBody() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/prompt/%s", userId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testSummarise_Successful() throws Exception {
        UUID userId = UUID.randomUUID();
        Prompt prompt = generatePrompt(userId);
        ATDPromptResponse promptResponse = generatePromptResponse(prompt);

        when(promptService.summariseWebPage(any(Prompt.class))).thenReturn(promptResponse);

        String body = convertToJson(prompt);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/prompt/summarise/%s", userId))
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.pageContent").value(prompt.getPageContent()))
                .andExpect(jsonPath("$.prompt.webUrl").value(prompt.getWebUrl()))
                .andExpect(jsonPath("$.prompt.id").exists())
                .andExpect(jsonPath("$.prompt.openAIResponse").exists())
                .andExpect(jsonPath("$.prompt.createdAt").exists())
                .andExpect(jsonPath("$.prompt.userId").value(userId.toString()));
    }

    @Test
    void testSummarise_NotLoggedIn() throws Exception {
        UUID userId = UUID.randomUUID();
        Prompt prompt = generatePrompt(userId);

        String body = convertToJson(prompt);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/prompt/summarise/%s", userId))
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testSummarise_EmptyBody() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/prompt/summarise/%s", userId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetPrompts_Successful() throws Exception {
        UUID userId = UUID.randomUUID();
        ATDSubscription subscription = generateActiveSubscription(userId);
        ATDPromptListResponse response = generatePromptListResponse(userId);

        when(subscriptionRepository.getSubscriptionByUserId(any(UUID.class))).thenReturn(subscription);
        when(promptService.getPrompts(any(), any(UUID.class), anyInt())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/prompt/retrieve")
                        .queryParam(USER_ID_PARAM, userId.toString())
                        .queryParam(WEB_URL_PARAM, WEB_URL)
                        .queryParam(MIN_PAGE_PARAM, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(response.getMessage()))
                .andExpect(jsonPath("$.prompts").isArray());
    }

    @Test
    void testGetPrompts_NotLoggedIn() throws Exception {
        UUID userId = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/prompt/retrieve")
                        .queryParam(USER_ID_PARAM, userId.toString())
                        .queryParam(WEB_URL_PARAM, WEB_URL)
                        .queryParam(MIN_PAGE_PARAM, "1")
                )
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testGetPrompts_MissingUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/prompt/retrieve")
                        .queryParam(WEB_URL_PARAM, WEB_URL)
                        .queryParam(MIN_PAGE_PARAM, "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetPrompts_MissingWebUrl() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/prompt/retrieve")
                        .queryParam(USER_ID_PARAM, userId.toString())
                        .queryParam(MIN_PAGE_PARAM, "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetPrompts_MissingMinPage() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/prompt/retrieve")
                        .queryParam(USER_ID_PARAM, userId.toString())
                        .queryParam(WEB_URL_PARAM, WEB_URL)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetPrompts_InvalidMinPage() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/prompt/retrieve")
                        .queryParam(USER_ID_PARAM, userId.toString())
                        .queryParam(WEB_URL_PARAM, WEB_URL)
                        .queryParam(MIN_PAGE_PARAM, "invalid")
                )
                .andExpect(status().isBadRequest());
    }

    private static ATDSubscription generateActiveSubscription(UUID userId) {
        ATDSubscription subscription = new ATDSubscription();
        subscription.setUserId(userId);
        subscription.setActive(true);

        return subscription;
    }

    private static Prompt generatePrompt(UUID userId) {
        Prompt prompt = new Prompt();
        prompt.setWebUrl(WEB_URL);
        prompt.setUserPrompt("This is a prompt");
        prompt.setPageContent("This is page content");
        prompt.setUserId(userId);

        return prompt;
    }

    private static ATDPromptResponse generatePromptResponse(Prompt prompt) {
        prompt.setOpenAIResponse("OpenAI response");
        prompt.setId(1L);
        prompt.setCreatedAt(LocalDateTime.now());

        ATDPromptResponse promptResponse = new ATDPromptResponse();
        promptResponse.setMessage("Successful prompt");
        promptResponse.setPrompt(prompt);

        return promptResponse;
    }

    private static ATDPromptListResponse generatePromptListResponse(UUID userId) {
        List<Prompt> prompts = new ArrayList<>();
        prompts.add(generatePrompt(userId));

        ATDPromptListResponse promptListResponse = new ATDPromptListResponse();
        promptListResponse.setPrompts(prompts);
        promptListResponse.setMessage("Successful retrieval");

        return promptListResponse;
    }
}
