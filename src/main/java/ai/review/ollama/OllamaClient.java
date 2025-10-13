package ai.review.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class OllamaClient {
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String apiUrl;
    private final String apiToken;
    private final String model;

    public OllamaClient(String apiUrl, String apiToken, String model) {
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        this.mapper = new ObjectMapper();
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
        this.model = model;
    }

    public String generate(String prompt) {
        try {
            // The endpoint looks like: POST /api/generate { model, prompt, stream:false }
            String payload = mapper.createObjectNode()
                    .put("model", model)
                    .put("prompt", prompt)
                    .put("stream", false)
                    .toString();
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json");
            if (apiToken != null && !apiToken.isBlank()) {
                b.header("Authorization", "Bearer " + apiToken);
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


