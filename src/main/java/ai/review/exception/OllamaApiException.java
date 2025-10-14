package ai.review.exception;

/**
 * Exception thrown when Ollama API operations fail.
 * This includes connection errors, model errors, and API errors.
 */
public class OllamaApiException extends RuntimeException {
    
    private final int statusCode;
    private final String model;
    
    public OllamaApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.model = null;
    }
    
    public OllamaApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.model = null;
    }
    
    public OllamaApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.model = null;
    }
    
    public OllamaApiException(String message, int statusCode, String model) {
        super(message);
        this.statusCode = statusCode;
        this.model = model;
    }
    
    public OllamaApiException(String message, int statusCode, String model, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.model = model;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getModel() {
        return model;
    }
    
    @Override
    public String getMessage() {
        if (statusCode > 0) {
            String modelInfo = model != null ? " (model: " + model + ")" : "";
            return String.format("%s (HTTP %d)%s", super.getMessage(), statusCode, modelInfo);
        }
        return super.getMessage();
    }
}
