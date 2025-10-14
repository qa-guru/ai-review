package ai.review.config;

import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import ai.review.service.ReviewService;
import ai.review.util.Env;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public GitHubClient gitHubClient() {
        String githubToken = Env.get("github.token", null);
        return new GitHubClient(githubToken);
    }
    
    @Bean
    public OllamaClient ollamaClient() {
        String ollamaUrl = Env.get("ollama.api.url", "https://autotests.ai/ollama/api/generate");
        String ollamaToken = Env.get("ollama.api.token", null);
        String ollamaModel = Env.get("ollama.model", "openchat:latest");
        return new OllamaClient(ollamaUrl, ollamaToken, ollamaModel);
    }
    
    @Bean
    public ReviewService reviewService(GitHubClient gitHubClient, OllamaClient ollamaClient) {
        return new ReviewService(gitHubClient, ollamaClient);
    }
}
