package org.acme.factories;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever.EmbeddingStoreContentRetrieverBuilder;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class ContentRetrieverFactory {

    private ContentRetrieverFactory() {
        throw new IllegalStateException("Factory class shouldn't be instantiated");
    }

    public static ContentRetriever createContentRetriever(EmbeddingModel embeddingModel,
                                                          EmbeddingStore<TextSegment> embeddingStore) {
        return getContentRetrieverBuilder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(10)
                .minScore(0.6)
                .build();
    }

    public static EmbeddingStoreContentRetrieverBuilder getContentRetrieverBuilder() {
        return EmbeddingStoreContentRetriever.builder();
    }
}
