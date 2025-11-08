package dev.langchain4j.model.embedding;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmbeddingModel {
    public static class SingleResult {
        private final Embedding embedding;

        public SingleResult(Embedding embedding) {
            this.embedding = embedding;
        }

        public Embedding content() {
            return embedding;
        }
    }

    public static class BatchResult {
        private final List<Embedding> list;

        public BatchResult(List<Embedding> list) {
            this.list = list;
        }

        public List<Embedding> content() {
            return list != null ? list : Collections.emptyList();
        }
    }

    public SingleResult embed(String text) {
        return new SingleResult(new Embedding());
    }

    public BatchResult embedAll(List<TextSegment> segments) {
        if (segments == null) return new BatchResult(Collections.emptyList());
        List<Embedding> list = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) list.add(new Embedding());
        return new BatchResult(list);
    }
}
