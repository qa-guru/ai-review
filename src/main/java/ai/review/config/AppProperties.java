package ai.review.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    
    @Valid
    @NotNull
    private GitHubProperties github = new GitHubProperties();
    
    @Valid
    @NotNull
    private OllamaProperties ollama = new OllamaProperties();
    
    public GitHubProperties getGitHub() {
        return github;
    }
    
    public void setGitHub(GitHubProperties github) {
        this.github = github;
    }
    
    public OllamaProperties getOllama() {
        return ollama;
    }
    
    public void setOllama(OllamaProperties ollama) {
        this.ollama = ollama;
    }
}
