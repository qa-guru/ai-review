# Exception Handling Strategy

This document describes the comprehensive exception handling strategy implemented in the GitHub PR Reviewer application.

## Overview

The application uses a layered exception handling approach with custom exceptions, a global exception handler, and proper HTTP status codes for consistent API responses.

## Custom Exceptions

### 1. GitHubApiException
- **Purpose**: Handles GitHub API related errors
- **HTTP Status**: 502 Bad Gateway (for API errors), 401/403/404 (for specific GitHub errors)
- **Usage**: Thrown when GitHub API calls fail (authentication, rate limiting, API errors)

### 2. OllamaApiException
- **Purpose**: Handles Ollama AI service related errors
- **HTTP Status**: 502 Bad Gateway
- **Usage**: Thrown when AI service calls fail (connection errors, model errors)

### 3. ReviewGenerationException
- **Purpose**: Handles business logic errors in review generation
- **HTTP Status**: 500 Internal Server Error
- **Usage**: Thrown when review generation fails or posting to GitHub fails

### 4. ValidationException
- **Purpose**: Handles input validation errors
- **HTTP Status**: 400 Bad Request
- **Usage**: Thrown when input parameters are invalid (repository format, PR number, etc.)

## Global Exception Handler

The `GlobalExceptionHandler` class provides centralized exception handling with:

- **Consistent Error Responses**: All errors return the same `ErrorResponse` format
- **Proper HTTP Status Codes**: Each exception type maps to appropriate HTTP status
- **Detailed Error Information**: Includes error type, message, timestamp, and request path
- **Field-Level Validation**: For validation errors, includes specific field information
- **Logging**: All exceptions are logged with appropriate levels (ERROR, WARN, DEBUG)

## Error Response Format

All API errors return a consistent JSON structure:

```json
{
  "error": "Error Type",
  "message": "Detailed error message",
  "status": 400,
  "path": "/api/review",
  "timestamp": "2024-01-15T10:30:00.000",
  "fieldErrors": [
    {
      "field": "repository",
      "rejectedValue": "invalid-repo",
      "message": "Repository must be in format 'owner/repo'"
    }
  ]
}
```

## HTTP Status Code Mapping

| Exception Type | HTTP Status | Description |
|----------------|-------------|-------------|
| ValidationException | 400 Bad Request | Invalid input parameters |
| GitHubApiException (401) | 401 Unauthorized | GitHub authentication failed |
| GitHubApiException (403) | 403 Forbidden | GitHub access denied |
| GitHubApiException (404) | 404 Not Found | GitHub resource not found |
| GitHubApiException (422) | 422 Unprocessable Entity | GitHub validation error |
| GitHubApiException (429) | 429 Too Many Requests | GitHub rate limit exceeded |
| GitHubApiException (5xx) | 502 Bad Gateway | GitHub server error |
| OllamaApiException | 502 Bad Gateway | AI service error |
| ReviewGenerationException | 500 Internal Server Error | Business logic error |
| IllegalArgumentException | 400 Bad Request | Invalid method arguments |
| IllegalStateException | 500 Internal Server Error | Service state error |
| Generic Exception | 500 Internal Server Error | Unexpected errors |

## Input Validation

The service layer includes comprehensive input validation:

### Repository Validation
- Must not be empty or null
- Must match format: `owner/repo` (alphanumeric, dots, underscores, hyphens allowed)

### PR Number Validation
- Must be a positive integer (> 0)

### Review Content Validation
- Must not be empty or null when posting to GitHub

## Logging Strategy

- **ERROR**: System errors, API failures, unexpected exceptions
- **WARN**: Validation errors, client errors, recoverable issues
- **INFO**: Business operations (review generation, posting)
- **DEBUG**: Detailed operation information, API calls

## Usage Examples

### Valid Request
```bash
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{
    "repository": "owner/repo",
    "prNumber": 123,
    "postToGitHub": false
  }'
```

### Invalid Repository Format
```bash
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{
    "repository": "invalid-repo-format",
    "prNumber": 123,
    "postToGitHub": false
  }'
```

**Response (400 Bad Request):**
```json
{
  "error": "Validation Error",
  "message": "Repository must be in format 'owner/repo' (Field: repository, Value: invalid-repo-format)",
  "status": 400,
  "path": "/api/review",
  "timestamp": "2024-01-15T10:30:00.000"
}
```

## Testing

The exception handling is thoroughly tested with unit tests covering:
- All custom exception types
- Global exception handler responses
- Input validation scenarios
- HTTP status code mapping
- Error response format validation

Run tests with:
```bash
./gradlew test
```

## Benefits

1. **Consistent API Responses**: All errors follow the same format
2. **Proper HTTP Status Codes**: Clients can handle errors appropriately
3. **Detailed Error Information**: Easy debugging and client error handling
4. **Centralized Error Handling**: Single point of error processing
5. **Comprehensive Logging**: Full audit trail of errors and operations
6. **Input Validation**: Prevents invalid data from reaching business logic
7. **Type Safety**: Custom exceptions provide type-safe error handling
