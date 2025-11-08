package dev.langchain4j.store.embedding;

import dev.langchain4j.data.embedding.Embedding;

import java.util.List;

public interface EmbeddingStore<T> {
    void addAll(List<Embedding> embeddings, List<T> items);
    EmbeddingSearchResult<T> search(EmbeddingSearchRequest request);
}
