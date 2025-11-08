package dev.langchain4j.store.embedding;

import java.util.Collections;
import java.util.List;

public class EmbeddingSearchResult<T> {
    private final List<EmbeddingMatch<T>> matches;

    public EmbeddingSearchResult(List<EmbeddingMatch<T>> matches) {
        this.matches = matches;
    }

    public List<EmbeddingMatch<T>> matches() { return matches != null ? matches : Collections.emptyList(); }
}
