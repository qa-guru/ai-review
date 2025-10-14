package ai.review.ollama;

import ai.review.config.OllamaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class OllamaClient {
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final OllamaProperties properties;

    public OllamaClient(OllamaProperties properties) {
        this.properties = properties;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .build();
        this.mapper = new ObjectMapper();
    }

    public String generate(String prompt) {
        try {
            // The endpoint looks like: POST /api/generate { model, prompt, stream:false }
            String payload = mapper.createObjectNode()
                    .put("model", properties.getModel())
                    .put("prompt", prompt)
                    .put("stream", false)
                    .toString();
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(properties.getApiUrl()))
                    .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                    .header("Content-Type", "application/json");
            if (properties.getApiToken() != null && !properties.getApiToken().isBlank()) {
                b.header("Authorization", "Bearer " + properties.getApiToken());
            }
            HttpRequest req = b.POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 300) {
                throw new RuntimeException("Ollama generate failed: " + resp.statusCode() + " " + resp.body());
            }
            JsonNode node = mapper.readTree(resp.body());
            // Common response field name: "response" or "text" depending on server
            if (node.hasNonNull("response")) {
                return node.get("response").asText();
            }
            if (node.hasNonNull("text")) {
                return node.get("text").asText();
            }
            return resp.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


