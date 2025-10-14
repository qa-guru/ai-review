package ai.review.exception;

/**
 * Exception thrown when review generation fails.
 * This is a business logic exception that wraps underlying technical issues.
 */
public class ReviewGenerationException extends RuntimeException {
    
    private final String repository;
    private final int prNumber;
    
    public ReviewGenerationException(String message) {
        super(message);
        this.repository = null;
        this.prNumber = 0;
    }
    
    public ReviewGenerationException(String message, Throwable cause) {
        super(message, cause);
        this.repository = null;
        this.prNumber = 0;
    }
    
    public ReviewGenerationException(String message, String repository, int prNumber) {
        super(message);
        this.repository = repository;
        this.prNumber = prNumber;
    }
    
    public ReviewGenerationException(String message, String repository, int prNumber, Throwable cause) {
        super(message, cause);
        this.repository = repository;
        this.prNumber = prNumber;
    }
    
    public String getRepository() {
        return repository;
    }
    
    public int getPrNumber() {
        return prNumber;
    }
    
    @Override
    public String getMessage() {
        if (repository != null && prNumber > 0) {
            return String.format("%s (Repository: %s, PR: #%d)", super.getMessage(), repository, prNumber);
        }
        return super.getMessage();
    }
}
