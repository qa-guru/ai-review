package ai.review.exception;

import ai.review.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler exceptionHandler;
    
    @Mock
    private HttpServletRequest request;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/review");
    }
    
    @Test
    void handleGitHubApiException_ShouldReturnBadGateway() {
        // Given
        GitHubApiException exception = new GitHubApiException(
            "GitHub API error", 
            500, 
            "Internal server error"
        );
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGitHubApiException(exception, request);
        
        // Then
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("GitHub API Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("GitHub API error"));
        assertEquals("/api/review", response.getBody().getPath());
    }
    
    @Test
    void handleOllamaApiException_ShouldReturnBadGateway() {
        // Given
        OllamaApiException exception = new OllamaApiException(
            "Ollama API error", 
            503, 
            "openchat:latest"
        );
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOllamaApiException(exception, request);
        
        // Then
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("AI Service Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Ollama API error"));
        assertEquals("/api/review", response.getBody().getPath());
    }
    
    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        // Given
        ValidationException exception = new ValidationException(
            "Invalid repository format", 
            "repository", 
            "invalid-repo"
        );
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, request);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Invalid repository format"));
        assertEquals("/api/review", response.getBody().getPath());
    }
    
    @Test
    void handleReviewGenerationException_ShouldReturnInternalServerError() {
        // Given
        ReviewGenerationException exception = new ReviewGenerationException(
            "Failed to generate review", 
            "owner/repo", 
            123
        );
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleReviewGenerationException(exception, request);
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Review Generation Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Failed to generate review"));
        assertEquals("/api/review", response.getBody().getPath());
    }
    
    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, request);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Argument", response.getBody().getError());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertEquals("/api/review", response.getBody().getPath());
    }
    
    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Given
        Exception exception = new Exception("Unexpected error");
        
        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, request);
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals("/api/review", response.getBody().getPath());
    }
}
