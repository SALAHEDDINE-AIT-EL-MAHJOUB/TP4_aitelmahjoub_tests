RAG-naif (démo locale)
=========================

Ce petit projet contient une implémentation autonome et pédagogique d'un RAG "naïf" sans dépendances externes.

Fichiers importants ajoutés:
- `src/main/java/ma/emsi/aitelmahjoub/tp2/Assistant.java` : interface simple de l'assistant.
- `src/main/java/ma/emsi/aitelmahjoub/tp2/RagNaifLocal.java` : démonstration locale du pipeline RAG (loader, splitter, embeddings simples, store, retriever, assistant).

But
----
Montrer les étapes clés d'un RAG sans appeler un LLM ni une vraie DB vectorielle. Utile pour comprendre et tester le flux avant d'intégrer LangChain4j.

Exécution
---------
1. Compiler : `mvn -q -DskipTests package` (ou via votre IDE).
2. Lancer la classe `ma.emsi.aitelmahjoub.tp2.RagNaifLocal`.
   - Vous pouvez fournir un chemin de fichier en argument ; sinon `infos.txt` sera cherché à la racine du projet et, si absent, un texte d'exemple sera utilisé.

Exemples
--------
java -cp target/TP4_aitelmahjoub_tests-1.0-SNAPSHOT.jar ma.emsi.aitelmahjoub.tp2.RagNaifLocal infos.txt

Notes
-----
- C'est une démonstration pédagogique : les embeddings sont déterministes mais primitifs.
- Pour une intégration réelle, utilisez LangChain4j + un modèle d'embeddings/external vector DB (Chroma, Weaviate...).
