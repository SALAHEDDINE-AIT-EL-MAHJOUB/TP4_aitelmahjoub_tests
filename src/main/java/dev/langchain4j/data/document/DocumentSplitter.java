package dev.langchain4j.data.document;

import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentSplitter {
    public List<TextSegment> split(Document document) {
        // Very small stub: return a single segment with document path as text
        if (document == null) return Collections.emptyList();
        List<TextSegment> list = new ArrayList<>();
        list.add(new TextSegment(document.toString()));
        return list;
    }
}
