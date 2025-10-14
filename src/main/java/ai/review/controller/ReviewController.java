package ai.review.controller;

import ai.review.dto.ReviewRequest;
import ai.review.dto.ReviewResponse;
import ai.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @PostMapping
    public ResponseEntity<ReviewResponse> generateReview(@Valid @RequestBody ReviewRequest request) {
        try {
            String review = reviewService.generateReview(request.getRepository(), request.getPrNumber());
            
            boolean postedToGitHub = false;
            String message = "Review generated successfully";
            
            if (request.isPostToGitHub()) {
                try {
                    reviewService.postReviewToGitHub(request.getRepository(), request.getPrNumber(), review);
                    postedToGitHub = true;
                    message = "Review generated and posted to GitHub PR #" + request.getPrNumber();
                } catch (Exception e) {
                    message = "Review generated but failed to post to GitHub: " + e.getMessage();
                }
            }
            
            ReviewResponse response = new ReviewResponse(review, postedToGitHub, message);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ReviewResponse errorResponse = new ReviewResponse(
                null, 
                false, 
                "Error generating review: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Review service is running");
    }
}
