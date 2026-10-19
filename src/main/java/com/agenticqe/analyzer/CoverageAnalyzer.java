package com.agenticqe.analyzer;

import com.agenticqe.model.Requirement;
import com.agenticqe.model.TestCase;
import com.agenticqe.matcher.CriteriaMatcher;

import java.util.ArrayList;
import java.util.List;

public class CoverageAnalyzer {
    private final CriteriaMatcher criteriaMatcher;
    public CoverageAnalyzer(CriteriaMatcher criteriaMatcher) {
        this.criteriaMatcher = criteriaMatcher;
    }
    public List<String> findMissingCriteria(
            Requirement requirement,
            List<TestCase> testCases) {

        // Start with all acceptance criteria as potentially missing.
        List<String> missingCriteria =
                new ArrayList<>(requirement.acceptanceCriteria());

        // Remove criteria that are already covered by existing test cases.
        for (TestCase testCase : testCases) {
            missingCriteria.removeIf(missingCriterion ->
                    testCase.coveredAcceptanceCriteria().stream()
                            .anyMatch(coveredCriterion ->
                                    criteriaMatcher.matches(missingCriterion, coveredCriterion))
            );
        }

        // Anything remaining represents a coverage gap.
        return missingCriteria;
    }
}
