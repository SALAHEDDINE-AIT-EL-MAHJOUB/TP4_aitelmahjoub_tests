package dev.langchain4j.data.document.parser.apache.tika;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;

import java.nio.file.Path;

public class ApacheTikaDocumentParser implements DocumentParser {
    @Override
    public Document parse(Path path) {
        // Minimal parser stub: wrap the path in Document
        return new Document(path);
    }
}
