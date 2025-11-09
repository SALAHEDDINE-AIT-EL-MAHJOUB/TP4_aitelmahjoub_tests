package dev.langchain4j.model.chat;

/**
 * Backwards-compatible marker interface for chat models.
 *
 * Extends {@link ChatLanguageModel} so implementations can be used
 * where a {@code ChatLanguageModel} is required by other components.
 */
public interface ChatModel extends ChatLanguageModel {
    // Marker interface for stubs
}
