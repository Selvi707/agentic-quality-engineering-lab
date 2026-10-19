package com.agenticqe.analyzer;
import com.agenticqe.matcher.NormalizedCriteriaMatcher;
import com.agenticqe.matcher.SemanticCriteriaMatcher;
import com.agenticqe.matcher.SemanticSimilarityService;
import com.agenticqe.model.Requirement;
import com.agenticqe.model.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class CoverageAnalyzerTest {
    @Test
    void shouldIdentifyMissingAcceptanceCriteria() {
        // ARRANGE
        // Create a requirement with three acceptance criteria that need test coverage.
        Requirement requirement = new Requirement(
                "QE-101",
                "Customer can make a payment",
                List.of(
                        "Valid payment succeeds",
                        "Invalid routing number is rejected",
                        "Confirmation is generated"
                )
        );
        // Create a test case that covers the successful payment acceptance criterion.
        TestCase testCase1 = new TestCase(
                "TC-001",
                "Successful payment",
                List.of("Valid payment succeeds")
        );
        // Create a second test case that covers the invalid routing number acceptance criterion.
        TestCase testCase2 = new TestCase(
                "TC-002",
                "Reject invalid routing number",
                List.of("Invalid routing number is rejected")
        );
        // Put the available test cases into the collection that will be analyzed.
        List<TestCase> testCases = List.of(testCase1, testCase2);
        // ACT
        // Ask CoverageAnalyzer to compare the requirement criteria
        // against the criteria covered by the available test cases.
        // CoverageAnalyzer analyzer = new CoverageAnalyzer();
        CoverageAnalyzer analyzer =
                new CoverageAnalyzer(new NormalizedCriteriaMatcher());

        List<String> missingCriteria =
                analyzer.findMissingCriteria(requirement, testCases);
        // ASSERT
        // The confirmation criterion has no corresponding test coverage,
        // so the analyzer should identify it as missing.
        assertEquals(
                List.of("Confirmation is generated"),
                missingCriteria
        );
    }

    @Test
    void shouldReturnEmptyListWhenAllCriteriaAreCovered() {

        // ARRANGE
        // Create a requirement with two acceptance criteria that need test coverage.
        Requirement requirement = new Requirement(
                "QE-102",
                "Customer can update contact information",
                List.of(
                        "Customer can update email",
                        "Customer can update phone number"
                )
        );

        // Create a test case that covers the email acceptance criterion.
        TestCase testCase1 = new TestCase(
                "TC-003",
                "Update customer email",
                List.of("Customer can update email")
        );

        // Create a second test case that covers the phone number acceptance criterion.
        TestCase testCase2 = new TestCase(
                "TC-004",
                "Update customer phone number",
                List.of("Customer can update phone number")
        );

        // Put both test cases into the collection that will be analyzed.
        List<TestCase> testCases = List.of(testCase1, testCase2);

        // ACT
        // Ask CoverageAnalyzer to compare the requirement criteria
        // against the criteria covered by the available test cases.
        //CoverageAnalyzer analyzer = new CoverageAnalyzer();
        //new CoverageAnalyzer(new NormalizedCriteriaMatcher());
        CoverageAnalyzer analyzer =
                new CoverageAnalyzer(new NormalizedCriteriaMatcher());

        List<String> missingCriteria =
                analyzer.findMissingCriteria(requirement, testCases);

        // ASSERT
        // Both acceptance criteria have test coverage,
        // so the analyzer should report no missing criteria.
        assertEquals(List.of(), missingCriteria);
    }
    @Test
    void shouldRecognizeSemanticallyEquivalentCriteria() {

        // ARRANGE
        // The requirement and test case describe the same behavior,
        // but they use different wording.
        Requirement requirement = new Requirement(
                "QE-103",
                "Invalid routing number should be rejected",
                List.of("Invalid routing number is rejected")
        );

        TestCase testCase = new TestCase(
                "TC-005",
                "Reject invalid routing number",
                List.of("Payment fails when bank routing number is invalid")
        );

        List<TestCase> testCases = List.of(testCase);

        // ACT
        //CoverageAnalyzer analyzer = new CoverageAnalyzer();
        //new CoverageAnalyzer(new NormalizedCriteriaMatcher());
        SemanticSimilarityService semanticSimilarityService =
                (firstText, secondText) -> true;
        CoverageAnalyzer analyzer =
                new CoverageAnalyzer(
                        new SemanticCriteriaMatcher(semanticSimilarityService));

        List<String> missingCriteria =
                analyzer.findMissingCriteria(requirement, testCases);

        // ASSERT
        // A human understands that these statements describe the same behavior.
        // Eventually, our analyzer should recognize that relationship too.
        assertEquals(List.of(), missingCriteria);
    }
    @Test
    void shouldIgnoreFormattingDifferences() {
        // ARRANGE
        Requirement requirement = new Requirement(
                "QE-104",
                "Invalid routing number should be rejected",
                List.of("Invalid routing number is rejected")
        );

        TestCase testCase = new TestCase(
                "TC-006",
                "Reject invalid routing number",
                List.of("  INVALID routing number\n   is rejected  ")
        );

        List<TestCase> testCases = List.of(testCase);

        // ACT
        //CoverageAnalyzer analyzer = new CoverageAnalyzer();
        //new CoverageAnalyzer(new NormalizedCriteriaMatcher());
        CoverageAnalyzer analyzer =
                new CoverageAnalyzer(new NormalizedCriteriaMatcher());

        List<String> missingCriteria =
                analyzer.findMissingCriteria(requirement, testCases);

        // ASSERT
        assertEquals(List.of(), missingCriteria);
    }
    @Test
    void shouldKeepCriterionMissingWhenNotSemanticallyEquivalent() {
        // ARRANGE
// Create a requirement that expects an invalid routing number to be rejected.
        Requirement requirement = new Requirement(
                "QE-105",
                "Invalid routing number should be rejected",
                List.of("Invalid routing number is rejected")
        );
        // Create a test case that covers a completely different behavior.
        TestCase testCase = new TestCase(
                "TC-006",
                "Update customer email",
                List.of("Customer can update email address")
        );

        List<TestCase> testCases = List.of(testCase);
        // ACT
       // Simulate a semantic service deciding that the two criteria are NOT equivalent.
        SemanticSimilarityService semanticSimilarityService =
                (String firstText, String secondText) -> false;
        CoverageAnalyzer analyzer =
                new CoverageAnalyzer(
                        new SemanticCriteriaMatcher(semanticSimilarityService));
        List<String> missingCriteria =
                analyzer.findMissingCriteria(requirement, testCases);
        // ASSERT
        // Because the semantic service says the criteria are NOT equivalent,
        // the requirement criterion should remain missing.
        assertEquals(
                List.of("Invalid routing number is rejected"),
                missingCriteria
        );
    }
}
