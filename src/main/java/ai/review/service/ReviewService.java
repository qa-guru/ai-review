package ai.review.service;

import ai.review.exception.ReviewGenerationException;
import ai.review.exception.ValidationException;
import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    
    private final GitHubClient gitHubClient;
    private final OllamaClient ollamaClient;

    public ReviewService(GitHubClient gitHubClient, OllamaClient ollamaClient) {
        this.gitHubClient = gitHubClient;
        this.ollamaClient = ollamaClient;
    }

    public String generateReview(String repo, int prNumber) {
        return generateReview(repo, prNumber, "prompt-template.txt");
    }
    
    public String generateReview(String repo, int prNumber, String templateName) {
        logger.info("Generating review for repository: {}, PR: {}, template: {}", repo, prNumber, templateName);
        
        // Validate input parameters
        validateRepository(repo);
        validatePrNumber(prNumber);
        
        try {
            String diff = gitHubClient.getPullRequestDiff(repo, prNumber);
            if (!StringUtils.hasText(diff)) {
                throw new ReviewGenerationException(
                    "No diff content found for pull request",
                    repo,
                    prNumber
                );
            }
            
            String prompt = buildPrompt(diff, templateName);
            String review = ollamaClient.generate(prompt);
            
            if (!StringUtils.hasText(review)) {
                throw new ReviewGenerationException(
                    "AI service returned empty review",
                    repo,
                    prNumber
                );
            }
            
            logger.info("Successfully generated review for repository: {}, PR: {}, template: {}", repo, prNumber, templateName);
            return review;
            
        } catch (Exception e) {
            if (e instanceof ReviewGenerationException) {
                throw e;
            }
            throw new ReviewGenerationException(
                "Failed to generate review: " + e.getMessage(),
                repo,
                prNumber,
                e
            );
        }
    }
    
    public void postReviewToGitHub(String repo, int prNumber, String review) {
        logger.info("Posting review to GitHub for repository: {}, PR: {}", repo, prNumber);
        
        // Validate input parameters
        validateRepository(repo);
        validatePrNumber(prNumber);
        
        if (!StringUtils.hasText(review)) {
            throw new ValidationException("Review content cannot be empty", "review", review);
        }
        
        try {
            gitHubClient.postIssueComment(repo, prNumber, review);
            logger.info("Successfully posted review to GitHub for repository: {}, PR: {}", repo, prNumber);
        } catch (Exception e) {
            throw new ReviewGenerationException(
                "Failed to post review to GitHub: " + e.getMessage(),
                repo,
                prNumber,
                e
            );
        }
    }
    
    /**
     * Validate repository format (should be owner/repo)
     */
    private void validateRepository(String repo) {
        if (!StringUtils.hasText(repo)) {
            throw new ValidationException("Repository cannot be empty", "repository", repo);
        }
        
        if (!repo.matches("^[a-zA-Z0-9._-]+/[a-zA-Z0-9._-]+$")) {
            throw new ValidationException(
                "Repository must be in format 'owner/repo'", 
                "repository", 
                repo
            );
        }
    }
    
    /**
     * Validate PR number
     */
    private void validatePrNumber(int prNumber) {
        if (prNumber <= 0) {
            throw new ValidationException(
                "Pull request number must be positive", 
                "prNumber", 
                prNumber
            );
        }
    }

    private String loadPromptTemplate(String templateName) {
        try {
            ClassPathResource resource = new ClassPathResource(templateName);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to load prompt template: {}", templateName, e);
            throw new ReviewGenerationException("Failed to load prompt template: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String unifiedDiff, String templateName) {
        String template = loadPromptTemplate(templateName);
        return template.replace("{DIFF_CONTENT}", unifiedDiff);
    }
}


