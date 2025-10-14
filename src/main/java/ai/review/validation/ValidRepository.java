package ai.review.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for GitHub repository format.
 * Validates that the repository string is in the format "owner/repo".
 */
@Documented
@Constraint(validatedBy = RepositoryValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRepository {
    
    String message() default "Repository must be in format 'owner/repo' (e.g., 'octocat/Hello-World')";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
