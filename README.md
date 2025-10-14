# GitHub PR Reviewer - Spring Boot Service

A Spring Boot REST service that automatically reviews GitHub pull requests using AI.

## Features

- REST API endpoint for generating PR reviews
- Integration with GitHub API to fetch PR diffs
- AI-powered code review using Ollama
- Flexible prompt template system:
  - Specify any `.txt` template file name in the request
  - Built-in templates: `prompt-template.txt` (general), `qa-automation-prompt-template.txt` (QA automation)
  - Easy to add custom templates by placing `.txt` files in resources
- Optional automatic posting of reviews to GitHub PRs

## Configuration

Create a `config.properties` file in the root directory or set environment variables:

```properties
# GitHub configuration
github.token=your_github_token_here

# Ollama configuration
ollama.api.url=https://autotests.ai/ollama/api/generate
ollama.api.token=your_ollama_token_here
ollama.model=openchat:latest
```

## Running the Service

### Build and Run
```bash
./gradlew bootRun
```

The service will start on port 8080.

### Using Docker (optional)
```bash
./gradlew bootJar
docker build -t github-pr-reviewer .
docker run -p 8080:8080 github-pr-reviewer
```

## API Usage

### Generate Review

**POST** `/api/review`

Request body:
```json
{
  "repository": "owner/repo",
  "prNumber": 123,
  "postToGitHub": false,
  "templateName": "prompt-template.txt"
}
```

**Template Names:**
- `prompt-template.txt` (default): Standard code review for Java code
- `qa-automation-prompt-template.txt`: Specialized review for test automation code
- Any custom `.txt` file placed in `src/main/resources/`

Response:
```json
{
  "review": "Generated review text...",
  "postedToGitHub": false,
  "message": "Review generated successfully"
}
```

### Health Check

**GET** `/api/review/health`

Response: `"Review service is running"`

## Example Usage

```bash
# Generate review without posting (using default template)
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{
    "repository": "svasenkov/niffler-ai-tests",
    "prNumber": 1,
    "postToGitHub": false
  }'

# Generate review with QA automation template
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{
    "repository": "svasenkov/niffler-ai-tests",
    "prNumber": 1,
    "postToGitHub": false,
    "templateName": "qa-automation-prompt-template.txt"
  }'

# Generate and post review to GitHub with custom template
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{
    "repository": "svasenkov/niffler-ai-tests", 
    "prNumber": 1,
    "postToGitHub": true,
    "templateName": "prompt-template.txt"
  }'
```

## Environment Variables

- `GITHUB_TOKEN`: GitHub personal access token (required for posting reviews)
- `OLLAMA_API_URL`: Ollama API endpoint (default: https://autotests.ai/ollama/api/generate)
- `OLLAMA_API_TOKEN`: Ollama API token (if required)
- `OLLAMA_MODEL`: Model to use for reviews (default: openchat:latest)
