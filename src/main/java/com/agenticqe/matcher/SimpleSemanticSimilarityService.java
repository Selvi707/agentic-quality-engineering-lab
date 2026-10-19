package com.agenticqe.matcher;

public class SimpleSemanticSimilarityService implements SemanticSimilarityService {
    @Override
    public boolean areSemanticallyEquivalent(
            String firstText,
            String secondText) {
        String normalizedFirstText = firstText.toLowerCase();
        String normalizedSecondText = secondText.toLowerCase();

        boolean oppositeMeaning =
                (normalizedFirstText.contains("approved")
                        && normalizedSecondText.contains("rejected"))
                        ||
                        (normalizedFirstText.contains("rejected")
                                && normalizedSecondText.contains("approved"));

        if (oppositeMeaning) {
            return false;
        }

        String[] firstWords = normalizedFirstText.split("\\s+");
        String[] secondWords = normalizedSecondText.split("\\s+");
        int matchingWords = 0;
        for (String firstWord : firstWords) {

            for (String secondWord : secondWords) {
                if (firstWord.equals(secondWord)) {
                    matchingWords++;
                }
            }
        }
        int shorterLength = Math.min(firstWords.length, secondWords.length);
        double similarity = (double) matchingWords / shorterLength;
        return similarity >= 0.60;
    }
}
