package ai.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing the generated AI code review")
public class ReviewResponse {
    
    @Schema(
        description = "The generated AI code review text",
        example = "This is a well-structured pull request with good code quality. The changes follow best practices and include appropriate tests."
    )
    private String review;
    
    @Schema(
        description = "Whether the review was successfully posted to GitHub",
        example = "true"
    )
    private boolean postedToGitHub;
    
    @Schema(
        description = "Status message describing the result of the operation",
        example = "Review generated and posted to GitHub PR #123"
    )
    private String message;
    
    public ReviewResponse() {}
    
    public ReviewResponse(String review, boolean postedToGitHub, String message) {
        this.review = review;
        this.postedToGitHub = postedToGitHub;
        this.message = message;
    }
    
    public String getReview() {
        return review;
    }
    
    public void setReview(String review) {
        this.review = review;
    }
    
    public boolean isPostedToGitHub() {
        return postedToGitHub;
    }
    
    public void setPostedToGitHub(boolean postedToGitHub) {
        this.postedToGitHub = postedToGitHub;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
