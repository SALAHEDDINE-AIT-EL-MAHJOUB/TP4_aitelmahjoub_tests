package dev.langchain4j.data.embedding;

public class Embedding {
    private final double[] vector;

    public Embedding() {
        this.vector = new double[0];
    }

    public double[] vector() {
        return vector;
    }
}
