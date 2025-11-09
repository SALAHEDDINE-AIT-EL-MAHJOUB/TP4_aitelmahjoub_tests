package ma.emsi.aitelmahjoub.test5;
import ma.emsi.aitelmahjoub.assistant.Assistant;
import dev.langchain4j.data.document.*;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;

import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class Test5_Web {
    private static void configureLogger() {
        Logger logger = Logger.getLogger("dev.langchain4j");
        logger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
    }
    public static void main(String[] args) {
        configureLogger();
        DocumentParser parser = new ApacheTikaDocumentParser();
        Path path = Paths.get("src/main/resources/RAG.pdf");
        Document doc = FileSystemDocumentLoader.loadDocument(path, parser);

        var splitter = DocumentSplitters.recursive(300, 30);
        List<TextSegment> segments = splitter.split(doc);
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        store.addAll(embeddings, segments);


        String GEMINI_KEY = System.getenv("GEMINI_KEY");
        if (GEMINI_KEY == null) throw new IllegalStateException("GEMINI_KEY manquant !");
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(GEMINI_KEY)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .logRequestsAndResponses(true)
                .build();

        //  ContentRetriever local (embeddings)
        EmbeddingStoreContentRetriever retrieverLocal = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();

        //  Création du moteur Tavily
        String TAVILY_KEY = System.getenv("TAVILY_API_KEY");
        List<ContentRetriever> retrievers = new ArrayList<>();
        retrievers.add(retrieverLocal);

        if (TAVILY_KEY != null && !TAVILY_KEY.isBlank()) {
            var tavilyEngine = TavilyWebSearchEngine.builder()
                    .apiKey(TAVILY_KEY)
                    .build();
            ContentRetriever retrieverWeb = WebSearchContentRetriever.builder()
                    .webSearchEngine(tavilyEngine)
                    .maxResults(3)
                    .build();
            retrievers.add(retrieverWeb);
        } else {
            System.err.println("Warning: TAVILY_API_KEY not set. Web search retriever disabled.");
        }

        // QueryRouter : combine PDF (+ Web if available)
        QueryRouter router = new DefaultQueryRouter(retrievers);

        //  RetrievalAugmentor
        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(router)
                .build();

        // Création de l’assistant
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(augmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        //  Interaction console
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\n Vous : ");
                String q = scanner.nextLine();
                if (q.equalsIgnoreCase("exit")) break;
                String r = assistant.chat(q);
                System.out.println(" Gemini : " + r);
            }
        }
    }
}