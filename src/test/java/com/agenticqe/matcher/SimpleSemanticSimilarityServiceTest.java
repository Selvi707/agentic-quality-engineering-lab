package com.agenticqe.matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleSemanticSimilarityServiceTest
{
    @Test
    void shouldRecognizeSemanticallyEquivalentText() {

        // ARRANGE
        String requirementCriterion =
                "Invalid routing number is rejected";

        String testCriterion =
                "Payment fails when routing number is invalid";

        SimpleSemanticSimilarityService service =
                new SimpleSemanticSimilarityService();
        // ACT
        boolean result =
                service.areSemanticallyEquivalent(
                        requirementCriterion,
                        testCriterion
                );
        // ASSERT
        assertTrue(result);

    }
    @Test
    void shouldNotTreatOppositeMeaningAsEquivalent() {

        // ARRANGE
        String firstCriterion =
                "Customer payment is approved";

        String secondCriterion =
                "Customer payment is rejected";

        SimpleSemanticSimilarityService service =
                new SimpleSemanticSimilarityService();

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
    void shouldNotTreatDifferentBusinessMeaningAsEquivalent() {

        // ARRANGE
        String firstCriterion =
                "Customer payment is approved";

        String secondCriterion =
                "Customer account is approved";

        SimpleSemanticSimilarityService service =
                new SimpleSemanticSimilarityService();

        // ACT
        boolean result =
                service.areSemanticallyEquivalent(
                        firstCriterion,
                        secondCriterion
                );

        // ASSERT
        assertFalse(result);
    }
}
