package ai.review.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class GitHubClient {
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String token; // may be null for read-only

    public GitHubClient(String token) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.mapper = new ObjectMapper();
        this.token = token;
    }

    public JsonNode getPullRequest(String repo, int prNumber) {
        String url = "https://api.github.com/repos/" + repo + "/pulls/" + prNumber;
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/vnd.github+json");
        if (token != null && !token.isBlank()) {
            b.header("Authorization", "Bearer " + token);
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
        String url = "https://api.github.com/repos/" + repo + "/pulls/" + prNumber;
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/vnd.github.v3.diff");
        if (token != null && !token.isBlank()) {
            b.header("Authorization", "Bearer " + token);
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
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("GITHUB_TOKEN is required to post comments");
        }
        String url = "https://api.github.com/repos/" + repo + "/issues/" + prNumber + "/comments";
        try {
            String payload = mapper.createObjectNode().put("body", body).toString();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer " + token)
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


