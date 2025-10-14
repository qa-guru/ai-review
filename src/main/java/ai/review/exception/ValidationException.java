package ai.review.exception;

/**
 * Exception thrown when input validation fails.
 * This includes invalid repository format, invalid PR numbers, etc.
 */
public class ValidationException extends RuntimeException {
    
    private final String field;
    private final Object value;
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public ValidationException(String message, String field, Object value) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    public ValidationException(String message, String field, Object value, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
    
    @Override
    public String getMessage() {
        if (field != null && value != null) {
            return String.format("%s (Field: %s, Value: %s)", super.getMessage(), field, value);
        }
        return super.getMessage();
    }
}
