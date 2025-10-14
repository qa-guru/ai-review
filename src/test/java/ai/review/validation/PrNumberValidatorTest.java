package ai.review.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PrNumberValidatorTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void validPrNumber_Minimum_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass(1);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validPrNumber_Maximum_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass(999999);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validPrNumber_MiddleRange_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass(12345);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void invalidPrNumber_Zero_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass(0);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Pull request number 0 must be at least 1"));
    }
    
    @Test
    void invalidPrNumber_Negative_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass(-1);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Pull request number -1 must be at least 1"));
    }
    
    @Test
    void invalidPrNumber_TooLarge_ShouldFailValidation() {
        // Given
        TestClass testClass = new TestClass(1000000);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Pull request number 1000000 exceeds maximum allowed value of 999999"));
    }
    
    @Test
    void nullPrNumber_ShouldPassValidation() {
        // Given
        TestClass testClass = new TestClass(null);
        
        // When
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        
        // Then
        assertTrue(violations.isEmpty());
    }
    
    // Test class to validate the annotation
    private static class TestClass {
        @ValidPrNumber
        private final Integer prNumber;
        
        public TestClass(Integer prNumber) {
            this.prNumber = prNumber;
        }
        
        public Integer getPrNumber() {
            return prNumber;
        }
    }
}
