package ai.review.exception;

import ai.review.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the application.
 * Provides consistent error responses and proper HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle GitHub API exceptions
     */
    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ErrorResponse> handleGitHubApiException(
            GitHubApiException ex, 
            HttpServletRequest request) {
        
        logger.error("GitHub API error: {}", ex.getMessage(), ex);
        
        HttpStatus status = determineHttpStatus(ex.getStatusCode());
        ErrorResponse errorResponse = new ErrorResponse(
            "GitHub API Error",
            ex.getMessage(),
            status.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle Ollama API exceptions
     */
    @ExceptionHandler(OllamaApiException.class)
    public ResponseEntity<ErrorResponse> handleOllamaApiException(
            OllamaApiException ex, 
            HttpServletRequest request) {
        
        logger.error("Ollama API error: {}", ex.getMessage(), ex);
        
        HttpStatus status = determineHttpStatus(ex.getStatusCode());
        ErrorResponse errorResponse = new ErrorResponse(
            "AI Service Error",
            ex.getMessage(),
            status.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle review generation exceptions
     */
    @ExceptionHandler(ReviewGenerationException.class)
    public ResponseEntity<ErrorResponse> handleReviewGenerationException(
            ReviewGenerationException ex, 
            HttpServletRequest request) {
        
        logger.error("Review generation error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Review Generation Error",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, 
            HttpServletRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Validation Error",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle method argument validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        logger.warn("Method argument validation error: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ErrorResponse.FieldError(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
            ));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Validation Error",
            "Invalid request parameters",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        errorResponse.setFieldErrors(fieldErrors);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, 
            HttpServletRequest request) {
        
        logger.warn("Constraint violation error: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            fieldErrors.add(new ErrorResponse.FieldError(
                fieldName,
                violation.getInvalidValue(),
                violation.getMessage()
            ));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Validation Error",
            "Invalid request parameters",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        errorResponse.setFieldErrors(fieldErrors);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle missing request parameter exceptions
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, 
            HttpServletRequest request) {
        
        logger.warn("Missing request parameter: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Missing Parameter",
            String.format("Required parameter '%s' is missing", ex.getParameterName()),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle method argument type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, 
            HttpServletRequest request) {
        
        logger.warn("Method argument type mismatch: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Type Mismatch",
            String.format("Parameter '%s' should be of type %s", 
                ex.getName(), 
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle HTTP message not readable exceptions (malformed JSON)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, 
            HttpServletRequest request) {
        
        logger.warn("HTTP message not readable: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Invalid Request Body",
            "Request body is malformed or invalid",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle HTTP request method not supported exceptions
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, 
            HttpServletRequest request) {
        
        logger.warn("HTTP method not supported: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Method Not Allowed",
            String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()),
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
    
    /**
     * Handle no handler found exceptions (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, 
            HttpServletRequest request) {
        
        logger.warn("No handler found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Not Found",
            String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            HttpServletRequest request) {
        
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Invalid Argument",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, 
            HttpServletRequest request) {
        
        logger.error("Illegal state: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Service Error",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle all other unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Determine HTTP status code based on API status code
     */
    private HttpStatus determineHttpStatus(int apiStatusCode) {
        if (apiStatusCode == 0) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return switch (apiStatusCode) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 422 -> HttpStatus.UNPROCESSABLE_ENTITY;
            case 429 -> HttpStatus.TOO_MANY_REQUESTS;
            case 500, 502, 503, 504 -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
