package ai.review.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * Validator implementation for GitHub repository format validation.
 * Validates that the repository string follows the "owner/repo" format.
 */
public class RepositoryValidator implements ConstraintValidator<ValidRepository, String> {
    
    // GitHub repository name pattern: owner/repo
    // - Owner and repo names can contain alphanumeric characters, dots, underscores, and hyphens
    // - Must not start or end with a dot, underscore, or hyphen
    // - Must contain exactly one forward slash
    private static final String REPOSITORY_PATTERN = "^[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?/[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?$";
    
    @Override
    public void initialize(ValidRepository constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String repository, ConstraintValidatorContext context) {
        // Allow null values - use @NotNull annotation separately if required
        if (!StringUtils.hasText(repository)) {
            return true;
        }
        
        // Check if repository matches the expected format
        boolean isValid = repository.matches(REPOSITORY_PATTERN);
        
        if (!isValid) {
            // Customize the error message
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Repository '%s' must be in format 'owner/repo' (e.g., 'octocat/Hello-World')", repository)
            ).addConstraintViolation();
        }
        
        return isValid;
    }
}
