package ai.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available prompt template types for code review generation")
public enum PromptTemplateType {
    
    @Schema(description = "General code review template for Java code")
    GENERAL("prompt-template.txt", "General code review"),
    
    @Schema(description = "QA automation focused template for test code review")
    QA_AUTOMATION("qa-automation-prompt-template.txt", "QA automation review");
    
    private final String fileName;
    private final String description;
    
    PromptTemplateType(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getDescription() {
        return description;
    }
}
