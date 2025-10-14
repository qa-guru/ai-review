package ai.review.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for GitHub pull request number validation.
 * Validates that the PR number is within reasonable bounds.
 */
public class PrNumberValidator implements ConstraintValidator<ValidPrNumber, Integer> {
    
    private static final int MIN_PR_NUMBER = 1;
    private static final int MAX_PR_NUMBER = 999999; // Reasonable upper bound
    
    @Override
    public void initialize(ValidPrNumber constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(Integer prNumber, ConstraintValidatorContext context) {
        // Allow null values - use @NotNull annotation separately if required
        if (prNumber == null) {
            return true;
        }
        
        // Check if PR number is within valid range
        boolean isValid = prNumber >= MIN_PR_NUMBER && prNumber <= MAX_PR_NUMBER;
        
        if (!isValid) {
            // Customize the error message
            context.disableDefaultConstraintViolation();
            if (prNumber < MIN_PR_NUMBER) {
                context.buildConstraintViolationWithTemplate(
                    String.format("Pull request number %d must be at least %d", prNumber, MIN_PR_NUMBER)
                ).addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate(
                    String.format("Pull request number %d exceeds maximum allowed value of %d", prNumber, MAX_PR_NUMBER)
                ).addConstraintViolation();
            }
        }
        
        return isValid;
    }
}
