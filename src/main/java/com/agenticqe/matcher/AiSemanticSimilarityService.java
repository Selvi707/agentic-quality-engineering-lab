package com.agenticqe.matcher;

public class AiSemanticSimilarityService
        implements SemanticSimilarityService {

    private final SemanticAiClient semanticAiClient;

    public AiSemanticSimilarityService(
            SemanticAiClient semanticAiClient) {

        this.semanticAiClient = semanticAiClient;
    }

    @Override
    public boolean areSemanticallyEquivalent(
            String firstText,
            String secondText) {

        return semanticAiClient.areSemanticallyEquivalent(
                firstText,
                secondText
        );
    }
}