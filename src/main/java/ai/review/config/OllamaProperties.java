package ai.review.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ConfigurationProperties(prefix = "app.ollama")
@Validated
public class OllamaProperties {
    
    @NotBlank(message = "Ollama API URL is required")
    private String apiUrl = "https://autotests.ai/ollama/api/generate";
    
    private String apiToken;
    
    @NotBlank(message = "Ollama model is required")
    private String model = "openchat:latest";
    
    @NotNull(message = "Connection timeout is required")
    @Positive(message = "Connection timeout must be positive")
    private Integer connectTimeoutSeconds = 15;
    
    @NotNull(message = "Request timeout is required")
    @Positive(message = "Request timeout must be positive")
    private Integer requestTimeoutSeconds = 60;
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getApiToken() {
        return apiToken;
    }
    
    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
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
