package ai.review.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReviewRequestValidationTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void validRequest_ShouldPassAllValidations() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", 123, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validRequestWithCustomTemplate_ShouldPassAllValidations() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", 123, false, "qa-automation-prompt-template.txt");
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void invalidTemplateName_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", 123, false, "invalid-template");
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("templateName", violation.getPropertyPath().toString());
        assertEquals("Invalid template name", violation.getMessage());
    }
    
    @Test
    void templateNameWithPathSeparator_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", 123, false, "folder/template.txt");
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("templateName", violation.getPropertyPath().toString());
        assertEquals("Invalid template name", violation.getMessage());
    }
    
    @Test
    void invalidRepository_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("invalid-repo-format", 123, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("repository", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("Repository 'invalid-repo-format' must be in format 'owner/repo'"));
    }
    
    @Test
    void nullRepository_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest(null, 123, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("repository", violation.getPropertyPath().toString());
        assertEquals("Repository is required", violation.getMessage());
    }
    
    @Test
    void emptyRepository_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("", 123, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("repository", violation.getPropertyPath().toString());
        assertEquals("Repository is required", violation.getMessage());
    }
    
    @Test
    void nullPrNumber_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", null, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("prNumber", violation.getPropertyPath().toString());
        assertEquals("Pull request number is required", violation.getMessage());
    }
    
    @Test
    void zeroPrNumber_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", 0, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        
        // Check that both @Positive and @ValidPrNumber validations fail
        boolean hasPositiveViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Pull request number must be positive"));
        boolean hasValidPrNumberViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Pull request number 0 must be at least 1"));
        
        assertTrue(hasPositiveViolation);
        assertTrue(hasValidPrNumberViolation);
    }
    
    @Test
    void negativePrNumber_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", -1, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        
        // Check that both @Positive and @ValidPrNumber validations fail
        boolean hasPositiveViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Pull request number must be positive"));
        boolean hasValidPrNumberViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Pull request number -1 must be at least 1"));
        
        assertTrue(hasPositiveViolation);
        assertTrue(hasValidPrNumberViolation);
    }
    
    @Test
    void tooLargePrNumber_ShouldFailValidation() {
        // Given
        ReviewRequest request = new ReviewRequest("octocat/Hello-World", 1000000, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ReviewRequest> violation = violations.iterator().next();
        assertEquals("prNumber", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("Pull request number 1000000 exceeds maximum allowed value of 999999"));
    }
    
    @Test
    void multipleValidationErrors_ShouldReturnAllViolations() {
        // Given
        ReviewRequest request = new ReviewRequest("invalid-repo", -5, false);
        
        // When
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size()); // repository format + 2 PR number validations
        
        // Check repository validation
        boolean hasRepositoryViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("repository"));
        assertTrue(hasRepositoryViolation);
        
        // Check PR number validations
        boolean hasPositiveViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Pull request number must be positive"));
        boolean hasValidPrNumberViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Pull request number -5 must be at least 1"));
        
        assertTrue(hasPositiveViolation);
        assertTrue(hasValidPrNumberViolation);
    }
}
