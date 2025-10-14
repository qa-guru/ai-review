package ai.review.validation;

import ai.review.dto.PromptTemplateType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TemplateTypeValidator implements ConstraintValidator<ValidTemplateType, PromptTemplateType> {
    
    @Override
    public void initialize(ValidTemplateType constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(PromptTemplateType templateType, ConstraintValidatorContext context) {
        if (templateType == null) {
            return true; // null values are handled by @NotNull if needed
        }
        
        // Check if the template type is valid (all enum values are valid)
        try {
            PromptTemplateType.valueOf(templateType.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
