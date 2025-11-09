package ma.emsi.aitelmahjoub.test4;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AssistantLimité {

    @SystemMessage("""
    Tu peux utiliser le RAG uniquement si la question de l’utilisateur concerne tes domaines d’expertise.
    Si le message n’a aucun rapport (ex : « bonjour », « ça va ? »),
    réponds simplement de manière naturelle, sans consulter le RAG.
        """)
    String chat(@UserMessage String message);
}