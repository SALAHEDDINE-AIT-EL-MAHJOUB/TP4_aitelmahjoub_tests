package dev.langchain4j.store.embedding.inmemory;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryEmbeddingStore<T> implements EmbeddingStore<T> {
    private final List<Embedding> embeddings = new ArrayList<>();
    private final List<T> items = new ArrayList<>();

    @Override
    public void addAll(List<Embedding> embeddings, List<T> items) {
        if (embeddings != null) this.embeddings.addAll(embeddings);
        if (items != null) this.items.addAll(items);
    }

    @Override
    public EmbeddingSearchResult<T> search(EmbeddingSearchRequest request) {
        // Very small stub: return empty result
        return new EmbeddingSearchResult<>(Collections.emptyList());
    }
}
