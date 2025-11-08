package dev.langchain4j.rag.content.retriever;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class EmbeddingStoreContentRetriever {
    private final EmbeddingStore<?> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final int maxResults;
    private final double minScore;

    private EmbeddingStoreContentRetriever(EmbeddingStore<?> embeddingStore, EmbeddingModel embeddingModel, int maxResults, double minScore) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.maxResults = maxResults;
        this.minScore = minScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EmbeddingStore<?> embeddingStore;
        private EmbeddingModel embeddingModel;
        private int maxResults = 5;
        private double minScore = 0.0;

        public Builder embeddingStore(EmbeddingStore<?> s) { this.embeddingStore = s; return this; }
        public Builder embeddingModel(EmbeddingModel m) { this.embeddingModel = m; return this; }
        public Builder maxResults(int r) { this.maxResults = r; return this; }
        public Builder minScore(double s) { this.minScore = s; return this; }
        public EmbeddingStoreContentRetriever build() { return new EmbeddingStoreContentRetriever(embeddingStore, embeddingModel, maxResults, minScore); }
    }
}
