package dev.langchain4j.data.document.splitter;

import dev.langchain4j.data.document.DocumentSplitter;

public class DocumentSplitters {
    public static DocumentSplitter recursive(int size, int overlap) {
        return new DocumentSplitter();
    }
}
