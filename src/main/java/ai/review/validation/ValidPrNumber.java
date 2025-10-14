package ai.review.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for GitHub pull request numbers.
 * Validates that the PR number is a positive integer within reasonable bounds.
 */
@Documented
@Constraint(validatedBy = PrNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrNumber {
    
    String message() default "Pull request number must be a positive integer between 1 and 999999";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
