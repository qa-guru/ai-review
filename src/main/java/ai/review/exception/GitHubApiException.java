package ai.review.exception;

/**
 * Exception thrown when GitHub API operations fail.
 * This includes authentication errors, rate limiting, and API errors.
 */
public class GitHubApiException extends RuntimeException {
    
    private final int statusCode;
    private final String apiError;
    
    public GitHubApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.apiError = null;
    }
    
    public GitHubApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.apiError = null;
    }
    
    public GitHubApiException(String message, int statusCode, String apiError) {
        super(message);
        this.statusCode = statusCode;
        this.apiError = apiError;
    }
    
    public GitHubApiException(String message, int statusCode, String apiError, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.apiError = apiError;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getApiError() {
        return apiError;
    }
    
    @Override
    public String getMessage() {
        if (statusCode > 0 && apiError != null) {
            return String.format("%s (HTTP %d: %s)", super.getMessage(), statusCode, apiError);
        }
        return super.getMessage();
    }
}
