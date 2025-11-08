package dev.langchain4j.model.googleai;

import dev.langchain4j.model.chat.ChatModel;

public class GoogleAiGeminiChatModel implements ChatModel {
    private final String apiKey;
    private final double temperature;
    private final boolean logRequestsAndResponses;
    private final String modelName;

    private GoogleAiGeminiChatModel(String apiKey, double temperature, boolean logRequestsAndResponses, String modelName) {
        this.apiKey = apiKey;
        this.temperature = temperature;
        this.logRequestsAndResponses = logRequestsAndResponses;
        this.modelName = modelName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String apiKey;
        private double temperature;
        private boolean logRequestsAndResponses;
        private String modelName;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder logRequestsAndResponses(boolean v) {
            this.logRequestsAndResponses = v;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public GoogleAiGeminiChatModel build() {
            return new GoogleAiGeminiChatModel(apiKey, temperature, logRequestsAndResponses, modelName);
        }
    }
}
