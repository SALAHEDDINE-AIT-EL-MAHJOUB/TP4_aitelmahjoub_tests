package dev.langchain4j.data.document;

public interface DocumentParser {
    Document parse(java.nio.file.Path path);
}
