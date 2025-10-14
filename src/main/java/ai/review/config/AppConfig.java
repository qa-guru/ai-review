package ai.review.config;

import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import ai.review.service.ReviewService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {
    
    @Bean
    public GitHubClient gitHubClient(AppProperties appProperties) {
        return new GitHubClient(appProperties.getGitHub());
    }
    
    @Bean
    public OllamaClient ollamaClient(AppProperties appProperties) {
        return new OllamaClient(appProperties.getOllama());
    }
    
    @Bean
    public ReviewService reviewService(GitHubClient gitHubClient, OllamaClient ollamaClient) {
        return new ReviewService(gitHubClient, ollamaClient);
    }
}
