package ai.review.dto;

public class ReviewResponse {
    
    private String review;
    private boolean postedToGitHub;
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
