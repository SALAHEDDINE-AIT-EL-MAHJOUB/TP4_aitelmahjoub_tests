package ma.emsi.aitelmahjoub.test3;

import ma.emsi.aitelmahjoub.assistant.Assistant;
import dev.langchain4j.data.document.*;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRoutage {

    private static void configureLogger() {
        System.out.println("Configuring logger");
        Logger packageLogger = Logger.getLogger("dev.langchain4j");
        packageLogger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        packageLogger.addHandler(handler);
    }

    public static void main(String[] args) {
        configureLogger();
        System.out.println("=== Test 3 : Routage ===");

        DocumentParser parser = new ApacheTikaDocumentParser();
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        List<TextSegment> segmentsIA = loadAndSplit("src/main/resources/rag.pdf", parser);
        List<TextSegment> segmentsMusique = loadAndSplit("src/main/resources/music.pdf", parser);

        EmbeddingStore<TextSegment> storeIA = new InMemoryEmbeddingStore<>();
        EmbeddingStore<TextSegment> storeMusique = new InMemoryEmbeddingStore<>();

        storeIA.addAll(embeddingModel.embedAll(segmentsIA).content(), segmentsIA);
        storeMusique.addAll(embeddingModel.embedAll(segmentsMusique).content(), segmentsMusique);

        var retrieverIA = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(storeIA)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.5)
                .build();

        var retrieverMusique = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(storeMusique)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.5)
                .build();

        String key = System.getenv("GEMINI_KEY");
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(key)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .logRequestsAndResponses(true)
                .build();

        Map<ContentRetriever, String> desc = new HashMap<>();
        desc.put(retrieverIA, "Documents de cours sur le RAG, le fine-tuning et l'intelligence artificielle");
        desc.put(retrieverMusique, "Document sur kotlin, son utilisation, ses extensions et son importance ");

        var queryRouter = new LanguageModelQueryRouter(model, desc);

        var augmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(augmentor)
                .build();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\nVous : ");
            String question = sc.nextLine();
            if (question.equalsIgnoreCase("exit")) break;

            String reponse = assistant.chat(question);
            System.out.println("Gemini : " + reponse);
        }
    }

    private static List<TextSegment> loadAndSplit(String chemin, DocumentParser parser) {
        Path path = Paths.get(chemin);
        Document doc = FileSystemDocumentLoader.loadDocument(path, parser);
        return DocumentSplitters.recursive(300, 30).split(doc);
    }
}