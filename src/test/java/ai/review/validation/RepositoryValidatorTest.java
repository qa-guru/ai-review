package ai.review.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryValidatorTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void validRepository_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass("octocat/Hello-World");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validRepositoryWithNumbers_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass("user123/repo-456");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validRepositoryWithDotsAndUnderscores_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass("user_name/repo.name");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validRepositoryWithHyphens_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass("user-name/repo-name");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void invalidRepository_NoSlash_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass("invalid-repo-format");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Repository 'invalid-repo-format' must be in format 'owner/repo'"));
    }
    
    @Test
    void invalidRepository_MultipleSlashes_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass("owner/repo/subdir");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Repository 'owner/repo/subdir' must be in format 'owner/repo'"));
    }
    
    @Test
    void invalidRepository_StartsWithSpecialChar_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass("_owner/repo");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Repository '_owner/repo' must be in format 'owner/repo'"));
    }
    
    @Test
    void invalidRepository_EndsWithSpecialChar_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass("owner/repo_");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Repository 'owner/repo_' must be in format 'owner/repo'"));
    }
    
    @Test
    void invalidRepository_EmptyOwner_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass("/repo");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Repository '/repo' must be in format 'owner/repo'"));
    }
    
    @Test
    void invalidRepository_EmptyRepo_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass("owner/");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Repository 'owner/' must be in format 'owner/repo'"));
    }
    
    @Test
    void nullRepository_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass(null);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void emptyRepository_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass("");
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    // Test class to validate the annotation
    private static class TestClass {
        @ValidRepository
        private final String repository;
        
        public TestClass(String repository) {
            this.repository = repository;
        }
        
        public String getRepository() {
            return repository;
        }
    }
}
