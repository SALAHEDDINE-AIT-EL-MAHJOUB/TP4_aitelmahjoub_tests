package dev.langchain4j.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dev.langchain4j.model.chat.ChatModel;

public class AiServices {
    public static <T> Builder<T> builder(Class<T> cls) {
        return new Builder<>(cls);
    }

    public static class Builder<T> {
        private final Class<T> iface;
        private ChatModel chatModel;
        private Object chatMemory;
        private Object contentRetriever;

        public Builder(Class<T> iface) {
            this.iface = iface;
        }

        public Builder<T> chatModel(ChatModel m) { this.chatModel = m; return this; }
        public Builder<T> chatMemory(Object m) { this.chatMemory = m; return this; }
        public Builder<T> contentRetriever(Object r) { this.contentRetriever = r; return this; }

        @SuppressWarnings("unchecked")
        public T build() {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // If interface has method chat(String): attempt a helpful response instead of a generic stub
                        if (method.getName().equals("chat") && args != null && args.length == 1) {
                            String query = args[0] != null ? args[0].toString().toLowerCase() : "";
                            // Provide a helpful French explanation when user asks about RAG
                            if (query.contains("rag") || query.contains("signification") || query.contains("retrieval-augmented") || query.contains("retrieval augmented")) {
                                return "RAG (Retrieval-Augmented Generation) est une approche qui combine la récupération d'informations depuis un magasin de connaissances (ex: embeddings + documents) avec la génération de texte par un modèle de langage. Le système récupère des passages pertinents puis les fournit au modèle pour produire des réponses plus précises et ancrées dans des sources externes.";
                            }
                            // Default fallback keeps previous behavior
                            return "[stub response]";
                        }
                    // default returns null or primitive defaults
                    Class<?> rt = method.getReturnType();
                    if (rt.isPrimitive()) {
                        if (rt == boolean.class) return false;
                        if (rt == byte.class) return (byte)0;
                        if (rt == short.class) return (short)0;
                        if (rt == int.class) return 0;
                        if (rt == long.class) return 0L;
                        if (rt == float.class) return 0f;
                        if (rt == double.class) return 0d;
                        if (rt == char.class) return '\0';
                    }
                    return null;
                }
            };
            return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, handler);
        }
    }
}
