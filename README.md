# GitHub PR Reviewer (Java)

CLI tool that fetches a GitHub Pull Request diff, sends it to an Ollama-compatible API to generate a review in Russian, and optionally posts the review back to the PR.

## Requirements
- Java 17+
- Maven 3.8+

## Configuration
You can configure the bot in two ways:

### Option 1: Configuration File (Recommended)
Create a `config.properties` file in the same directory as the JAR:

```properties
# GitHub Personal Access Token (required for posting reviews)
github.token=your_github_token_here

# Ollama API Configuration
ollama.api.url=https://autotests.ai/ollama/api/generate
ollama.api.token=your_ollama_token_here
ollama.model=openchat:latest
```

### Option 2: Environment Variables
Set the following environment variables:

- `GITHUB_TOKEN` – required to post a review/comment. If omitted, the tool will print the review to stdout only.
- `OLLAMA_API_URL` – default: `https://autotests.ai/ollama/api/generate`
- `OLLAMA_API_TOKEN` – API token for the Ollama endpoint.
- `OLLAMA_MODEL` – default: `openchat:latest`

**Note**: Environment variables take precedence over the config file.

## Build
```bash
./gradlew -q shadowJar
```

This produces a shaded JAR at:
```
./build/libs/github-pr-reviewer-0.1.0-all.jar
```

## Usage
```bash
java -jar build/libs/github-pr-reviewer-0.1.0-all.jar <repo> <pr_number> [post]
```

Examples:

**Using config.properties file:**
```bash
# Create config.properties with your tokens, then:
java -jar build/libs/github-pr-reviewer-0.1.0-all.jar svasenkov/niffler-ai-tests 1
java -jar build/libs/github-pr-reviewer-0.1.0-all.jar svasenkov/niffler-ai-tests 1 true
```

**Using environment variables:**
```bash
export OLLAMA_API_URL='https://autotests.ai/ollama/api/generate'
export OLLAMA_API_TOKEN='sk-xxx'
export OLLAMA_MODEL='openchat:latest'
export GITHUB_TOKEN='ghp_xxx'

java -jar build/libs/github-pr-reviewer-0.1.0-all.jar svasenkov/niffler-ai-tests 1 true
```

## Language Support

The tool generates code reviews in **Russian language** by default. The AI model is instructed to:
- Provide reviews in Russian only
- Include: 1) Brief summary, 2) Strengths, 3) Risks/Bugs, 4) Suggestions, 5) Security/Performance notes if any
- Format as concise bullet points

## Notes
- When the third argument (`post`) is omitted or `GITHUB_TOKEN` is not set, the app prints the generated review to stdout.
- The tool uses GitHub REST API to fetch PR diff and to create review comments.

## License
MIT
