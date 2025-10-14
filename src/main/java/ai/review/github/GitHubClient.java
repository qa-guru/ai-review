package ai.review.github;

import ai.review.config.GitHubProperties;
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
public class GitHubClient {
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final GitHubProperties properties;

    public GitHubClient(GitHubProperties properties) {
        this.properties = properties;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .build();
        this.mapper = new ObjectMapper();
    }

    public JsonNode getPullRequest(String repo, int prNumber) {
        String url = properties.getBaseUrl() + "/repos/" + repo + "/pulls/" + prNumber;
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                .header("Accept", "application/vnd.github+json");
        if (properties.getToken() != null && !properties.getToken().isBlank()) {
            b.header("Authorization", "Bearer " + properties.getToken());
        }
        try {
            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 300) {
                throw new RuntimeException("GitHub getPullRequest failed: " + resp.statusCode() + " " + resp.body());
            }
            return mapper.readTree(resp.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPullRequestDiff(String repo, int prNumber) {
        String url = properties.getBaseUrl() + "/repos/" + repo + "/pulls/" + prNumber;
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                .header("Accept", "application/vnd.github.v3.diff");
        if (properties.getToken() != null && !properties.getToken().isBlank()) {
            b.header("Authorization", "Bearer " + properties.getToken());
        }
        try {
            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 300) {
                throw new RuntimeException("GitHub getPullRequestDiff failed: " + resp.statusCode() + " " + resp.body());
            }
            return resp.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void postIssueComment(String repo, int prNumber, String body) {
        if (properties.getToken() == null || properties.getToken().isBlank()) {
            throw new IllegalStateException("GitHub token is required to post comments");
        }
        String url = properties.getBaseUrl() + "/repos/" + repo + "/issues/" + prNumber + "/comments";
        try {
            String payload = mapper.createObjectNode().put("body", body).toString();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer " + properties.getToken())
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 300) {
                throw new RuntimeException("GitHub postIssueComment failed: " + resp.statusCode() + " " + resp.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


