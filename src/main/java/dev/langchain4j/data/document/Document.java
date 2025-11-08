package dev.langchain4j.data.document;

import java.nio.file.Path;

public class Document {
    private final Path path;

    public Document(Path path) {
        this.path = path;
    }

    public Path path() {
        return path;
    }

    @Override
    public String toString() {
        return "Document{" + path + '}';
    }
}
