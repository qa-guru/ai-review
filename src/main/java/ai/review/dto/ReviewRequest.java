package ai.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReviewRequest {
    
    @NotBlank(message = "Repository is required")
    private String repository;
    
    @NotNull(message = "Pull request number is required")
    @Positive(message = "Pull request number must be positive")
    private Integer prNumber;
    
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
