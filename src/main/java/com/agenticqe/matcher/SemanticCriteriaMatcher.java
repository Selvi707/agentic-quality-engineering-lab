package com.agenticqe.matcher;

public class SemanticCriteriaMatcher implements CriteriaMatcher {
    private final SemanticSimilarityService semanticSimilarityService;
    public SemanticCriteriaMatcher(
            SemanticSimilarityService semanticSimilarityService) {

        this.semanticSimilarityService = semanticSimilarityService;
    }
    @Override
    public boolean matches(String requirementCriterion, String testCriterion) {
        return semanticSimilarityService.areSemanticallyEquivalent(
                requirementCriterion,
                testCriterion
        );
    }

}
