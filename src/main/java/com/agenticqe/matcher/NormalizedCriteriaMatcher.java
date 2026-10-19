package com.agenticqe.matcher;

public class NormalizedCriteriaMatcher implements CriteriaMatcher{

    @Override
    public boolean matches(String requirementCriterion, String testCriterion) {
        return normalize(requirementCriterion)
                .equals(normalize(testCriterion));
    }
    private String normalize(String text) {
        return text
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");
    }
}
