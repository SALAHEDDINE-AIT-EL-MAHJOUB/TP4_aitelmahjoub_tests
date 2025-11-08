package ma.emsi.aitelmahjoub.test1;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import ma.emsi.aitelmahjoub.assistant.Assistant;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
public class RagNaif {

    
    private static void configureLogger() {
        // Configure the underlying java.util.logging logger used by langchain4j
        Logger packageLogger = Logger.getLogger("dev.langchain4j");
        packageLogger.setLevel(Level.FINE);

        // Console handler so logs appear in console
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        packageLogger.addHandler(handler);
    }

    public static void main(String[] args) {
        // configure logger early so LangChain4j logs (requests/responses) are shown
        configureLogger();
        try {
            /**
             * Phase 1 : Enregistrement des embeddings
             */

            System.out.println("=== Phase 1 : Enregistrement des embeddings ===");

            // 1. Récupération du Path du fichier PDF
            Path pdfPath = Paths.get("src/main/resources/RAG.pdf");
            System.out.println("Chargement du fichier : " + pdfPath);

            // 2. Création du parser PDF
            ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();

            // 3. Chargement du document
            Document document = FileSystemDocumentLoader.loadDocument(pdfPath, parser);
            System.out.println("Document chargé !");

            // 4. Création du DocumentSplitter et découpage en chunks
            DocumentSplitter splitter = DocumentSplitters.recursive(500, 0);
            List<TextSegment> segments = splitter.split(document);
            System.out.println("Document découpé en " + segments.size() + " segments");

            // 5. Création du modèle d'embedding
            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            // 6. Création des embeddings pour les segments
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            System.out.println(embeddings.size() + " embeddings créés");

            // 7. Stockage dans un magasin d'embeddings en mémoire
            EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
            embeddingStore.addAll(embeddings, segments);
            System.out.println("Embeddings stockés  avec succès ");

            System.out.println("------------------Phase 1 terminée -----------------------");

            /**
             * Phase 2 : Utilisation des embeddings pour répondre aux questions
             */

            System.out.println("\n-------- Phase 2 : Configuration de l'assistant RAG ");

        // Création du ChatModel (logging des requêtes/réponses activé)
        ChatModel model = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GeminiKey"))
            .modelName("gemini-2.5-flash")
            .logRequestsAndResponses(true)
            .temperature(0.2)
            .build();

            // Création du ContentRetriever avec EmbeddingStoreContentRetriever (builder)
            EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(3)
                    .minScore(0.7)
                    .build();

            // Ajout d'une mémoire pour 10 messages
            MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

            // Création de l'assistant via AiServices
            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatModel(model)
                    .chatMemory(chatMemory)
                    .contentRetriever(retriever)
                    .build();


            System.out.println("Assistant  configuré avec succès!");
            System.out.println("poser vos questions");

            // Boucle de questions-réponses
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\nPosez votre question (ou 'FIN' pour quitter) : ");
                String question = scanner.nextLine();

                if (question.equalsIgnoreCase("FIN")) {
                    break;
                }

                if (question.trim().isEmpty()) {
                    continue;
                }

                try {
                    String response = assistant.chat(question);
                    System.out.println("\n Réponse : " + response);
                } catch (Exception e) {
                    System.out.println("\n Erreur lors de la génération de la réponse : " + e.getMessage());
                }
            }

            scanner.close();
            System.out.println("Au revoir !");

        } catch (Exception e) {
            System.err.println(" Erreur lors de l'exécution : " + e.getMessage());
            e.printStackTrace();
        }
    }
}