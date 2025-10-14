package ai.review.github;

import ai.review.config.GitHubProperties;
import ai.review.exception.GitHubApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);
    
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
        logger.debug("Fetching pull request {} for repository {}", prNumber, repo);
        
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
                String errorMessage = extractErrorMessage(resp.body());
                throw new GitHubApiException(
                    "Failed to fetch pull request " + prNumber + " from repository " + repo,
                    resp.statusCode(),
                    errorMessage
                );
            }
            return mapper.readTree(resp.body());
        } catch (IOException | InterruptedException e) {
            throw new GitHubApiException(
                "Network error while fetching pull request " + prNumber + " from repository " + repo,
                e
            );
        }
    }

    public String getPullRequestDiff(String repo, int prNumber) {
        logger.debug("Fetching pull request diff {} for repository {}", prNumber, repo);
        
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
                String errorMessage = extractErrorMessage(resp.body());
                throw new GitHubApiException(
                    "Failed to fetch pull request diff " + prNumber + " from repository " + repo,
                    resp.statusCode(),
                    errorMessage
                );
            }
            return resp.body();
        } catch (IOException | InterruptedException e) {
            throw new GitHubApiException(
                "Network error while fetching pull request diff " + prNumber + " from repository " + repo,
                e
            );
        }
    }

    public void postIssueComment(String repo, int prNumber, String body) {
        logger.debug("Posting comment to pull request {} in repository {}", prNumber, repo);
        
        if (properties.getToken() == null || properties.getToken().isBlank()) {
            throw new GitHubApiException("GitHub token is required to post comments");
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
                String errorMessage = extractErrorMessage(resp.body());
                throw new GitHubApiException(
                    "Failed to post comment to pull request " + prNumber + " in repository " + repo,
                    resp.statusCode(),
                    errorMessage
                );
            }
        } catch (IOException | InterruptedException e) {
            throw new GitHubApiException(
                "Network error while posting comment to pull request " + prNumber + " in repository " + repo,
                e
            );
        }
    }
    
    /**
     * Extract error message from GitHub API response
     */
    private String extractErrorMessage(String responseBody) {
        try {
            JsonNode errorNode = mapper.readTree(responseBody);
            if (errorNode.has("message")) {
                return errorNode.get("message").asText();
            }
            if (errorNode.has("error")) {
                return errorNode.get("error").asText();
            }
        } catch (Exception e) {
            logger.debug("Failed to parse error response: {}", e.getMessage());
        }
        return responseBody;
    }
}


