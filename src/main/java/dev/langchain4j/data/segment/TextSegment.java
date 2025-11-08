package dev.langchain4j.data.segment;

public class TextSegment {
    private final String text;

    public TextSegment(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
