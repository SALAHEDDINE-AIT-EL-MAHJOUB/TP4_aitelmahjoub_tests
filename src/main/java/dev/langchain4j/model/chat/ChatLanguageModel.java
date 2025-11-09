package dev.langchain4j.model.chat;

/**
 * Marker interface representing a language-capable chat model.
 *
 * Some components in the project expect a {@code ChatLanguageModel} type.
 * Keeping this as a separate marker allows existing {@code ChatModel}
 * implementations to remain compatible by extending this interface.
 */
public interface ChatLanguageModel {
    // marker interface
}
