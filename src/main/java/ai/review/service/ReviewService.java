package ai.review.service;

import ai.review.exception.ReviewGenerationException;
import ai.review.exception.ValidationException;
import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        logger.info("Generating review for repository: {}, PR: {}", repo, prNumber);
        
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
            
            String prompt = buildPrompt(diff);
            String review = ollamaClient.generate(prompt);
            
            if (!StringUtils.hasText(review)) {
                throw new ReviewGenerationException(
                    "AI service returned empty review",
                    repo,
                    prNumber
                );
            }
            
            logger.info("Successfully generated review for repository: {}, PR: {}", repo, prNumber);
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

    private String buildPrompt(String unifiedDiff) {
        StringBuilder sb = new StringBuilder();
        sb.append("Вы эксперт по ревью Java кода. Отвечайте только на русском языке.\n");
        sb.append("Проведите ревью следующего GitHub PR diff.\n");
        sb.append("Предоставьте: 1) Краткое резюме, 2) Сильные стороны, 3) Риски/Ошибки, 4) Предложения, 5) Замечания по безопасности/производительности если есть.\n");
        sb.append("Форматируйте как краткие пункты списка.\n\n");
        sb.append("PR Unified Diff:\n");
        sb.append("```diff\n");
        // keep diff as-is (may be large)
        sb.append(unifiedDiff);
        sb.append("\n``" + "`\n");
        return sb.toString();
    }
}


