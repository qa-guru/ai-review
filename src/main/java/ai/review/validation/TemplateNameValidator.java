package ai.review.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class TemplateNameValidator implements ConstraintValidator<ValidTemplateName, String> {
    
    @Override
    public void initialize(ValidTemplateName constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String templateName, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(templateName)) {
            return true; // null/empty values are handled by @NotBlank if needed
        }
        
        // Check if template name is valid
        // Must be a .txt file and not contain path separators for security
        return templateName.endsWith(".txt") && 
               !templateName.contains("/") && 
               !templateName.contains("\\") &&
               templateName.length() > 4; // at least "x.txt"
    }
}
