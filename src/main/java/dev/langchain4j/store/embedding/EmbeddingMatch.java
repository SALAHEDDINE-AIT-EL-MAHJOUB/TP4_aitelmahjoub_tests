package dev.langchain4j.store.embedding;

public class EmbeddingMatch<T> {
    private final T embedded;
    private final double score;

    public EmbeddingMatch(T embedded, double score) {
        this.embedded = embedded;
        this.score = score;
    }

    public T embedded() { return embedded; }
    public double score() { return score; }
}
