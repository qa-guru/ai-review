package ai.review.controller;

import ai.review.dto.ReviewRequest;
import ai.review.dto.ReviewResponse;
import ai.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@Tag(name = "Review", description = "API for generating AI-powered code reviews for GitHub pull requests")
public class ReviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    
    private final ReviewService reviewService;
    
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @Operation(
        summary = "Generate AI code review",
        description = "Generates an AI-powered code review for a GitHub pull request. Optionally posts the review directly to GitHub."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review generated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReviewResponse.class),
                examples = @ExampleObject(
                    name = "Successful Review",
                    value = """
                    {
                        "review": "This is a well-structured pull request with good code quality...",
                        "postedToGitHub": true,
                        "message": "Review generated and posted to GitHub PR #123"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                        "message": "Validation failed",
                        "errors": [
                            {
                                "field": "repository",
                                "message": "Repository is required"
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                    {
                        "message": "An error occurred while generating the review"
                    }
                    """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<ReviewResponse> generateReview(
        @Parameter(
            description = "Review request containing repository, PR number, and posting preferences",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ReviewRequest.class),
                examples = @ExampleObject(
                    name = "Review Request Example",
                    value = """
                    {
                        "repository": "owner/repository",
                        "prNumber": 123,
                        "postToGitHub": true,
                        "templateName": "prompt-template.txt"
                    }
                    """
                )
            )
        )
        @Valid @RequestBody ReviewRequest request) {
        logger.info("Received review request for repository: {}, PR: {}, postToGitHub: {}, template: {}", 
            request.getRepository(), request.getPrNumber(), request.isPostToGitHub(), request.getTemplateName());
        
        String review = reviewService.generateReview(request.getRepository(), request.getPrNumber(), request.getTemplateName());
        
        boolean postedToGitHub = false;
        String message = "Review generated successfully";
        
        if (request.isPostToGitHub()) {
            reviewService.postReviewToGitHub(request.getRepository(), request.getPrNumber(), review);
            postedToGitHub = true;
            message = "Review generated and posted to GitHub PR #" + request.getPrNumber();
        }
        
        ReviewResponse response = new ReviewResponse(review, postedToGitHub, message);
        logger.info("Review request completed successfully for repository: {}, PR: {}", 
            request.getRepository(), request.getPrNumber());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Health check",
        description = "Check if the review service is running and healthy"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Service is healthy",
        content = @Content(
            mediaType = "text/plain",
            examples = @ExampleObject(
                name = "Health Response",
                value = "Review service is running"
            )
        )
    )
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Review service is running");
    }
}
