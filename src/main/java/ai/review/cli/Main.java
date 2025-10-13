package ai.review.cli;

import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import ai.review.service.ReviewService;
import ai.review.util.Env;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: java -jar app.jar <repo> <pr_number> [post]");
                System.err.println("Example: java -jar app.jar svasenkov/niffler-ai-tests 1 true");
                System.exit(1);
            }
            
            String repo = args[0];
            int prNumber = Integer.parseInt(args[1]);
            boolean post = args.length > 2 && Boolean.parseBoolean(args[2]);
            
            String githubToken = Env.get("github.token", null);
            String ollamaUrl = Env.get("ollama.api.url", "https://autotests.ai/ollama/api/generate");
            String ollamaToken = Env.get("ollama.api.token", null);
            String ollamaModel = Env.get("ollama.model", "openchat:latest");

            OllamaClient ollamaClient = new OllamaClient(ollamaUrl, ollamaToken, ollamaModel);
            GitHubClient gh = new GitHubClient(githubToken);
            ReviewService service = new ReviewService(gh, ollamaClient);

            String review = service.generateReview(repo, prNumber);

            if (post && githubToken != null && !githubToken.isBlank()) {
                gh.postIssueComment(repo, prNumber, review);
                System.out.println("Review posted to GitHub PR #" + prNumber + ".");
            } else {
                System.out.println("--- Generated Review (not posted) ---\n" + review);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}


