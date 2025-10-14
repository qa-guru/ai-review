package ai.review.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ConfigurationProperties(prefix = "app.github")
@Validated
public class GitHubProperties {
    
    @NotBlank(message = "GitHub API base URL is required")
    private String baseUrl = "https://api.github.com";
    
    private String token;
    
    @NotNull(message = "Connection timeout is required")
    @Positive(message = "Connection timeout must be positive")
    private Integer connectTimeoutSeconds = 15;
    
    @NotNull(message = "Request timeout is required")
    @Positive(message = "Request timeout must be positive")
    private Integer requestTimeoutSeconds = 30;
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Integer getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }
    
    public void setConnectTimeoutSeconds(Integer connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }
    
    public Integer getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }
    
    public void setRequestTimeoutSeconds(Integer requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }
}
