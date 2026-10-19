package com.agenticqe.matcher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class AiSemanticSimilarityServiceTest {
    @Test
    void shouldNotTreatDifferentBusinessMeaningAsEquivalent() {

        // ARRANGE
        String firstCriterion =
                "Customer payment is approved";

        String secondCriterion =
                "Customer account is approved";

        SemanticAiClient semanticAiClient =
                (firstText, secondText) -> false;

        AiSemanticSimilarityService service =
                new AiSemanticSimilarityService(semanticAiClient);

        // ACT
        boolean result =
                service.areSemanticallyEquivalent(
                        firstCriterion,
                        secondCriterion
                );

        // ASSERT
        assertFalse(result);
    }
    @Test
    void shouldRecognizeSemanticallyEquivalentCriteria() {

        // ARRANGE
        String firstCriterion =
                "Invalid routing number is rejected";

        String secondCriterion =
                "Payment fails when routing number is invalid";

        SemanticAiClient semanticAiClient =
                (firstText, secondText) -> true;

        AiSemanticSimilarityService service =
                new AiSemanticSimilarityService(semanticAiClient);

        // ACT
        boolean result =
                service.areSemanticallyEquivalent(
                        firstCriterion,
                        secondCriterion
                );

        // ASSERT
        assertTrue(result);
    }
}