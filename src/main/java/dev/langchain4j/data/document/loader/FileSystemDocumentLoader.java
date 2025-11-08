package dev.langchain4j.data.document.loader;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;

import java.nio.file.Path;

public class FileSystemDocumentLoader {
    public static Document loadDocument(Path path, DocumentParser parser) {
        if (parser != null) {
            return parser.parse(path);
        }
        return new Document(path);
    }
}
