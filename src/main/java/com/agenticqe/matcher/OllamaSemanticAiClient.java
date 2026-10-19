package com.agenticqe.matcher;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Asks a local AI model (running in Ollama) whether two sentences mean the same thing.
 */
public class OllamaSemanticAiClient implements SemanticAiClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "qwen2.5:3b";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public boolean areSemanticallyEquivalent(String firstText, String secondText) {

        // 1. Write the question for the AI model.
        String question = "Do these two acceptance criteria describe the same testable behavior? "
                + "Requirement: " + firstText + ". "
                + "Test: " + secondText + ". "
                + "Answer with only one word: YES or NO.";

        // 2. Put the question into the JSON format that Ollama expects.
        String requestBody = "{"
                + "\"model\": \"" + MODEL + "\", "
                + "\"prompt\": \"" + cleanForJson(question) + "\", "
                + "\"stream\": false, "
                + "\"options\": {\"temperature\": 0}"
                + "}";

        // 3. Send the request to Ollama and read the reply.
        String reply = sendToOllama(requestBody);

        // 4. Turn the reply into true (YES) or false (NO).
        return readYesOrNo(reply);
    }

    private String sendToOllama(String requestBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Ollama returned an error: " + response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new IllegalStateException("Could not talk to Ollama. Is it running?", e);
        }
    }

    // The reply looks like: {"model":"qwen2.5:3b","response":"YES","done":true,...}
    private boolean readYesOrNo(String reply) {
        String upperReply = reply.toUpperCase();
        if (upperReply.contains("\"RESPONSE\":\"YES")) {
            return true;
        }
        if (upperReply.contains("\"RESPONSE\":\"NO")) {
            return false;
        }
        throw new IllegalStateException("The model did not answer YES or NO: " + reply);
    }

    // Quotes and backslashes inside the text would break the JSON, so we escape them.
    private String cleanForJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}