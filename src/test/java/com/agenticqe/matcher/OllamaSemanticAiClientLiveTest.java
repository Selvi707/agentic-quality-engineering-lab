package com.agenticqe.matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OllamaSemanticAiClientLiveTest {

    private final OllamaSemanticAiClient client = new OllamaSemanticAiClient();

    @Test
    void shouldRecognizeParaphrasedCriteria() {
        assertTrue(client.areSemanticallyEquivalent(
                "Invalid routing number is rejected",
                "Payment fails when routing number is invalid"));
    }

    @Test
    void shouldNotMatchDifferentBusinessObject() {
        assertFalse(client.areSemanticallyEquivalent(
                "Customer payment is approved",
                "Customer account is approved"));
    }
}