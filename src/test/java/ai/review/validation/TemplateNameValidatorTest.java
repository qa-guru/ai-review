package ai.review.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateNameValidatorTest {
    
    private TemplateNameValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new TemplateNameValidator();
        validator.initialize(null);
    }
    
    @Test
    void testValidTemplateNames() {
        assertTrue(validator.isValid("prompt-template.txt", null));
        assertTrue(validator.isValid("qa-automation-prompt-template.txt", null));
        assertTrue(validator.isValid("custom-template.txt", null));
        assertTrue(validator.isValid("a.txt", null));
    }
    
    @Test
    void testInvalidTemplateNames() {
        assertFalse(validator.isValid("template", null)); // no .txt extension
        assertFalse(validator.isValid("template.txt/", null)); // contains path separator
        assertFalse(validator.isValid("template\\file.txt", null)); // contains backslash
        assertFalse(validator.isValid("folder/template.txt", null)); // contains forward slash
        assertFalse(validator.isValid(".txt", null)); // too short
        assertTrue(validator.isValid("", null)); // empty string is valid (handled by @NotBlank)
    }
    
    @Test
    void testNullTemplateName() {
        assertTrue(validator.isValid(null, null));
    }
}
