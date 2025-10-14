package ai.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response DTO for consistent API error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response for API errors")
public class ErrorResponse {
    
    @Schema(description = "Error type or category", example = "Validation Error")
    private String error;
    
    @Schema(description = "Human-readable error message", example = "Validation failed")
    private String message;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Request path that caused the error", example = "/api/review")
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:45.123")
    private LocalDateTime timestamp;
    
    @Schema(description = "List of field-specific validation errors")
    private List<FieldError> fieldErrors;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String error, String message, int status) {
        this();
        this.error = error;
        this.message = message;
        this.status = status;
    }
    
    public ErrorResponse(String error, String message, int status, String path) {
        this(error, message, status);
        this.path = path;
    }
    
    @Schema(description = "Field-specific validation error details")
    public static class FieldError {
        @Schema(description = "Name of the field that failed validation", example = "repository")
        private String field;
        
        @Schema(description = "The value that was rejected", example = "")
        private Object rejectedValue;
        
        @Schema(description = "Validation error message", example = "Repository is required")
        private String message;
        
        public FieldError() {}
        
        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
        
        public Object getRejectedValue() {
            return rejectedValue;
        }
        
        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    // Getters and setters
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
    
    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
