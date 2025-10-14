package ai.review.service;

import ai.review.github.GitHubClient;
import ai.review.ollama.OllamaClient;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final GitHubClient gitHubClient;
    private final OllamaClient ollamaClient;

    public ReviewService(GitHubClient gitHubClient, OllamaClient ollamaClient) {
        this.gitHubClient = gitHubClient;
        this.ollamaClient = ollamaClient;
    }

    public String generateReview(String repo, int prNumber) {
        String diff = gitHubClient.getPullRequestDiff(repo, prNumber);
        String prompt = buildPrompt(diff);
        return ollamaClient.generate(prompt);
    }
    
    public void postReviewToGitHub(String repo, int prNumber, String review) {
        gitHubClient.postIssueComment(repo, prNumber, review);
    }

    private String buildPrompt(String unifiedDiff) {
        StringBuilder sb = new StringBuilder();
        sb.append("Вы эксперт по ревью Java кода. Отвечайте только на русском языке.\n");
        sb.append("Проведите ревью следующего GitHub PR diff.\n");
        sb.append("Предоставьте: 1) Краткое резюме, 2) Сильные стороны, 3) Риски/Ошибки, 4) Предложения, 5) Замечания по безопасности/производительности если есть.\n");
        sb.append("Форматируйте как краткие пункты списка.\n\n");
        sb.append("PR Unified Diff:\n");
        sb.append("```diff\n");
        // keep diff as-is (may be large)
        sb.append(unifiedDiff);
        sb.append("\n``" + "`\n");
        return sb.toString();
    }
}


