package ai.review.service;

import ai.review.exception.ValidationException;
import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    
    private ReviewService reviewService;
    
    @Mock
    private GitHubClient gitHubClient;
    
    @Mock
    private OllamaClient ollamaClient;
    
    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(gitHubClient, ollamaClient);
    }
    
    @Test
    void generateReview_WithInvalidRepository_ShouldThrowValidationException() {
        // Given
        String invalidRepo = "invalid-repo-format";
        int prNumber = 123;
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reviewService.generateReview(invalidRepo, prNumber);
        });
        
        assertTrue(exception.getMessage().contains("Repository must be in format 'owner/repo'"));
        assertEquals("repository", exception.getField());
        assertEquals(invalidRepo, exception.getValue());
    }
    
    @Test
    void generateReview_WithEmptyRepository_ShouldThrowValidationException() {
        // Given
        String emptyRepo = "";
        int prNumber = 123;
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reviewService.generateReview(emptyRepo, prNumber);
        });
        
        assertTrue(exception.getMessage().contains("Repository cannot be empty"));
        assertEquals("repository", exception.getField());
        assertEquals(emptyRepo, exception.getValue());
    }
    
    @Test
    void generateReview_WithInvalidPrNumber_ShouldThrowValidationException() {
        // Given
        String validRepo = "owner/repo";
        int invalidPrNumber = 0;
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reviewService.generateReview(validRepo, invalidPrNumber);
        });
        
        assertTrue(exception.getMessage().contains("Pull request number must be positive"));
        assertEquals("prNumber", exception.getField());
        assertEquals(invalidPrNumber, exception.getValue());
    }
    
    @Test
    void generateReview_WithNegativePrNumber_ShouldThrowValidationException() {
        // Given
        String validRepo = "owner/repo";
        int negativePrNumber = -1;
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reviewService.generateReview(validRepo, negativePrNumber);
        });
        
        assertTrue(exception.getMessage().contains("Pull request number must be positive"));
        assertEquals("prNumber", exception.getField());
        assertEquals(negativePrNumber, exception.getValue());
    }
    
    @Test
    void postReviewToGitHub_WithEmptyReview_ShouldThrowValidationException() {
        // Given
        String validRepo = "owner/repo";
        int validPrNumber = 123;
        String emptyReview = "";
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reviewService.postReviewToGitHub(validRepo, validPrNumber, emptyReview);
        });
        
        assertTrue(exception.getMessage().contains("Review content cannot be empty"));
        assertEquals("review", exception.getField());
        assertEquals(emptyReview, exception.getValue());
    }
    
    @Test
    void postReviewToGitHub_WithNullReview_ShouldThrowValidationException() {
        // Given
        String validRepo = "owner/repo";
        int validPrNumber = 123;
        String nullReview = null;
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            reviewService.postReviewToGitHub(validRepo, validPrNumber, nullReview);
        });
        
        assertTrue(exception.getMessage().contains("Review content cannot be empty"));
        assertEquals("review", exception.getField());
        assertEquals(nullReview, exception.getValue());
    }
    
    @Test
    void generateReview_WithQaAutomationTemplate_ShouldUseCorrectTemplate() {
        // Given
        String validRepo = "owner/repo";
        int validPrNumber = 123;
        String mockDiff = "diff content";
        String expectedReview = "QA automation review";
        
        when(gitHubClient.getPullRequestDiff(validRepo, validPrNumber)).thenReturn(mockDiff);
        when(ollamaClient.generate(org.mockito.ArgumentMatchers.anyString())).thenReturn(expectedReview);
        
        // When
        String result = reviewService.generateReview(validRepo, validPrNumber, "qa-automation-prompt-template.txt");
        
        // Then
        assertEquals(expectedReview, result);
    }
    
    @Test
    void generateReview_WithGeneralTemplate_ShouldUseCorrectTemplate() {
        // Given
        String validRepo = "owner/repo";
        int validPrNumber = 123;
        String mockDiff = "diff content";
        String expectedReview = "General review";
        
        when(gitHubClient.getPullRequestDiff(validRepo, validPrNumber)).thenReturn(mockDiff);
        when(ollamaClient.generate(org.mockito.ArgumentMatchers.anyString())).thenReturn(expectedReview);
        
        // When
        String result = reviewService.generateReview(validRepo, validPrNumber, "prompt-template.txt");
        
        // Then
        assertEquals(expectedReview, result);
    }
}
