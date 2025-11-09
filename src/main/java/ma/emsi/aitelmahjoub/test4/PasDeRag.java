package ma.emsi.aitelmahjoub.test4;


import ma.emsi.aitelmahjoub.assistant.Assistant;
import dev.langchain4j.data.document.*;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.query.Query;


import java.nio.file.*;
import java.util.*;
import java.util.Scanner;

public class PasDeRag {

    public static void main(String[] args) {

        System.out.println("=== Phase 1 : Ingestion du document RAG ===");


        DocumentParser parser = new ApacheTikaDocumentParser();
        Path path = Paths.get("src/main/resources/rag-2.pdf");
        Document doc = FileSystemDocumentLoader.loadDocument(path, parser);


        var splitter = DocumentSplitters.recursive(300, 30);
        List<TextSegment> segments = splitter.split(doc);
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();


        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        store.addAll(embeddings, segments);
        System.out.println("Ingestion terminée avec " + segments.size() + " segments");

        System.out.println("\n=== Phase 2 : Chat avec routage conditionnel (RAG ou pas) ===");


        String GEMINI_KEY = System.getenv("GEMINI_KEY");
        if (GEMINI_KEY == null) throw new IllegalStateException("GEMINI_KEY manquant !");
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(GEMINI_KEY)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .logRequestsAndResponses(true)
                .build();


        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();

        // 6️ Création de la classe interne : routage basé sur une question posée au LM
        // Ici nous utilisons le même ChatModel, mais pour interroger le modèle
        // directement nous construisons un petit Assistant local (via AiServices)
        // qui n'utilise pas de RetrievalAugmentor. Le QueryRouter pose la question
        // suivante au modèle :
        // "Est-ce que la requête '...query...' porte sur l'IA ? Réponds seulement par 'oui', 'non' ou 'peut-être'."
        // Si la réponse est 'non' -> on renvoie une liste vide, sinon on renvoie le retriever.
        class QueryRouterBaséSurLM implements QueryRouter {

            private final ma.emsi.aitelmahjoub.assistant.Assistant lmAssistant;

            QueryRouterBaséSurLM() {
                // build a tiny assistant that uses the same chat model but no retrieval augmentor
                this.lmAssistant = AiServices.builder(ma.emsi.aitelmahjoub.assistant.Assistant.class)
                        .chatModel(model)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(1))
                        .build();
            }

            @Override
            public Collection<ContentRetriever> route(Query query) {
                String userQuery = query == null || query.text() == null ? "" : query.text();

                String prompt = String.format("Est-ce que la requête '%s' porte sur l'IA ? Réponds seulement par 'oui', 'non' ou 'peut-être'.", userQuery.replace("\n", " "));

                System.out.println("LM routing prompt -> " + prompt);

                String lmAnswer;
                try {
                    lmAnswer = lmAssistant.chat(prompt);
                } catch (Exception e) {
                    System.out.println("Erreur lors de l'appel au LM pour le routage : " + e.getMessage());
                    // En cas d'erreur, on préfère désactiver le RAG plutôt que risquer d'ajouter du contenu incorrect
                    return Collections.emptyList();
                }

                String normalized = lmAnswer == null ? "" : lmAnswer.trim().toLowerCase();
                System.out.println("Réponse du LM pour le routage : '" + lmAnswer + "'");

                if (normalized.startsWith("non")) {
                    System.out.println(" Décision du QueryRouter : non (le LM indique que la requête n'est pas sur l'IA)");
                    return Collections.emptyList();
                } else {
                    System.out.println(" Décision du QueryRouter : oui/peut-être (le LM indique que la requête concerne l'IA)");
                    return List.of(retriever);
                }
            }
        }

        QueryRouter queryRouter = new QueryRouterBaséSurLM();


        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();


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