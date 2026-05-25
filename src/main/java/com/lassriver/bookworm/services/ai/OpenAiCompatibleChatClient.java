package com.lassriver.bookworm.services.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class OpenAiCompatibleChatClient implements AiChatClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${bookworm.ai.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${bookworm.ai.api-key:}")
    private String apiKey;

    @Value("${bookworm.ai.model:deepseek-v4-flash}")
    private String model;

    @Override
    public void stream(List<AiMessage> messages, Consumer<String> onChunk) {
        if (apiKey == null || apiKey.isBlank()) {
            onChunk.accept("El asistente IA no esta configurado. Define DEEPSEEK_API_KEY o bookworm.ai.api-key.");
            return;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();
            HttpRequest request = HttpRequest.newBuilder(chatCompletionsUri())
                    .timeout(Duration.ofSeconds(90))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(buildBody(messages))))
                    .build();

            HttpResponse<java.io.InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessRuleException("El proveedor IA respondio con estado " + response.statusCode() + ".");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data:")) {
                        continue;
                    }
                    String data = line.substring(5).trim();
                    if (data.isBlank() || "[DONE]".equals(data)) {
                        continue;
                    }
                    String content = extractContent(data);
                    if (!content.isEmpty()) {
                        onChunk.accept(content);
                    }
                }
            }
        } catch (BusinessRuleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessRuleException("No se pudo contactar el proveedor IA.");
        }
    }

    private URI chatCompletionsUri() {
        String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return URI.create(cleanBase + "/chat/completions");
    }

    private Map<String, Object> buildBody(List<AiMessage> messages) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", true);
        body.put("messages", messages.stream()
                .map(message -> Map.of("role", message.role(), "content", message.content()))
                .toList());
        return body;
    }

    private String extractContent(String data) throws Exception {
        JsonNode root = objectMapper.readTree(data);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return "";
        }
        return choices.get(0).path("delta").path("content").asText("");
    }
}
