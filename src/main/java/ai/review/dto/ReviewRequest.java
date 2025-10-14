package ai.review.dto;

import ai.review.validation.ValidPrNumber;
import ai.review.validation.ValidRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request object for generating AI code reviews")
public class ReviewRequest {
    
    @Schema(
        description = "GitHub repository in format 'owner/repository'",
        example = "microsoft/vscode",
        required = true
    )
    @NotBlank(message = "Repository is required")
    @ValidRepository
    private String repository;
    
    @Schema(
        description = "Pull request number to review",
        example = "123",
        required = true,
        minimum = "1"
    )
    @NotNull(message = "Pull request number is required")
    @Positive(message = "Pull request number must be positive")
    @ValidPrNumber
    private Integer prNumber;
    
    @Schema(
        description = "Whether to automatically post the review to GitHub",
        example = "false",
        defaultValue = "false"
    )
    private boolean postToGitHub = false;
    
    public ReviewRequest() {}
    
    public ReviewRequest(String repository, Integer prNumber, boolean postToGitHub) {
        this.repository = repository;
        this.prNumber = prNumber;
        this.postToGitHub = postToGitHub;
    }
    
    public String getRepository() {
        return repository;
    }
    
    public void setRepository(String repository) {
        this.repository = repository;
    }
    
    public Integer getPrNumber() {
        return prNumber;
    }
    
    public void setPrNumber(Integer prNumber) {
        this.prNumber = prNumber;
    }
    
    public boolean isPostToGitHub() {
        return postToGitHub;
    }
    
    public void setPostToGitHub(boolean postToGitHub) {
        this.postToGitHub = postToGitHub;
    }
}
