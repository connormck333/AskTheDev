package com.devconnor.askthedev.services.prompt;

import com.devconnor.askthedev.exception.InvalidPromptException;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIService {

    private static final String MODEL_NAME = "gpt-4o-mini";

    private final OpenAIClient openAIClient;

    public OpenAIService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public OpenAIService() {
        Dotenv dotenv = Dotenv.configure().load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        openAIClient = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String sendPrompt(String pageContent, String prompt) {
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content("""
                        You are a software engineer expert. A user will provide you with a question based on the current tab that they have open.
                        Generally, this will be a documentation. You will be provided with the text content from this page.
                        This may contain irrelevant information as it has been parsed from the html of the webpage.
                        You should be able to recognise irrelevant content and ignore it.
                        It is your job to provide an accurate and relevant answer to the user's question using the provided web page text as context.
                        At times, a user may ask a question that has no relevancy to the provided context.
                        You can ignore the context and provide an answer as if there was no context attached.
                        You should not mention the relevancy of the question to the provided context.
                        You should quote the provided context when necessary to backup your point.
                        You should provide an answer in a markdown format.
                        """)
                .build();
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content("Here is the text of the current Chrome tab: " + pageContent
                        + ". Please provide an answer using the html as context to the following question / prompt: " + prompt)
                .build();
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addMessage(systemMessage)
                .addMessage(userMessage)
                .model(MODEL_NAME)
                .build();

        ChatCompletion chatCompletion;
        try {
            chatCompletion = openAIClient.chat().completions().create(params);
        } catch (Exception e) {
            throw new InvalidPromptException();
        }

        return getResponse(chatCompletion.choices());
    }

    public String summariseWebPage(String pageContent) {
        return sendPrompt(pageContent, "Summarise this web page.");
    }

    private String getResponse(List<ChatCompletion.Choice> choices) {
        if (
                choices != null
                && !choices.isEmpty()
                && choices.getFirst().message().content().isPresent()
        ) {
            return choices.getFirst().message().content().get();
        }

        throw new InvalidPromptException();
    }
}
