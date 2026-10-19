package com.agenticqe.matcher;

public interface SemanticAiClient {
    boolean areSemanticallyEquivalent(
            String firstText,
            String secondText
    );
}
