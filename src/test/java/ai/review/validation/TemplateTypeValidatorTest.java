package ai.review.validation;

import ai.review.dto.PromptTemplateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTypeValidatorTest {
    
    private TemplateTypeValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new TemplateTypeValidator();
        validator.initialize(null);
    }
    
    @Test
    void testValidTemplateTypes() {
        assertTrue(validator.isValid(PromptTemplateType.GENERAL, null));
        assertTrue(validator.isValid(PromptTemplateType.QA_AUTOMATION, null));
    }
    
    @Test
    void testNullTemplateType() {
        assertTrue(validator.isValid(null, null));
    }
}
