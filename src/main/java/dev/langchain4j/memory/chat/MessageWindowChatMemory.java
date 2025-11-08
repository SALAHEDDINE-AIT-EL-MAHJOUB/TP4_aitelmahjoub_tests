package dev.langchain4j.memory.chat;

public class MessageWindowChatMemory {
    private final int maxMessages;

    private MessageWindowChatMemory(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public static MessageWindowChatMemory withMaxMessages(int maxMessages) {
        return new MessageWindowChatMemory(maxMessages);
    }
}
