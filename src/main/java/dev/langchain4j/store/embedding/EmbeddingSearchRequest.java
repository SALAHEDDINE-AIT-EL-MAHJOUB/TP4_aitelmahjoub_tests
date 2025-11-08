package dev.langchain4j.store.embedding;

import dev.langchain4j.data.embedding.Embedding;

public class EmbeddingSearchRequest {
    private final Embedding queryEmbedding;
    private final int maxResults;
    private final double minScore;

    private EmbeddingSearchRequest(Embedding embedding, int maxResults, double minScore) {
        this.queryEmbedding = embedding;
        this.maxResults = maxResults;
        this.minScore = minScore;
    }

    public Embedding queryEmbedding() { return queryEmbedding; }
    public int maxResults() { return maxResults; }
    public double minScore() { return minScore; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Embedding queryEmbedding;
        private int maxResults = 5;
        private double minScore = 0.0;

        public Builder queryEmbedding(Embedding e) { this.queryEmbedding = e; return this; }
        public Builder maxResults(int m) { this.maxResults = m; return this; }
        public Builder minScore(double s) { this.minScore = s; return this; }
        public EmbeddingSearchRequest build() { return new EmbeddingSearchRequest(queryEmbedding, maxResults, minScore); }
    }
}
