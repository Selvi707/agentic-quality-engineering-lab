# Agentic Quality Engineering Lab

A Java 21 / JUnit 5 lab exploring how AI can help answer a core QA question: **which acceptance criteria are not yet covered by tests?**

Requirements and test cases describe the same behavior in different words. Exact text comparison misses those matches, so this lab compares acceptance criteria through a pluggable set of matchers, from strict text rules to an LLM that judges whether two statements describe the same testable behavior.

## How it works

```
CoverageAnalyzer
   └── CriteriaMatcher (interface)
         ├── NormalizedCriteriaMatcher      exact match after trimming, lower-casing and collapsing whitespace
         └── SemanticCriteriaMatcher
               └── SemanticSimilarityService (interface)
                     ├── SimpleSemanticSimilarityService   keyword-overlap heuristic (≥ 60%) with an approved/rejected guard
                     └── AiSemanticSimilarityService
                           └── SemanticAiClient (interface)
                                 ├── OllamaSemanticAiClient   local model (qwen2.5:3b) via the Ollama HTTP API
                                 └── ClaudeSemanticAiClient   Anthropic Claude (in progress)
```

`CoverageAnalyzer.findMissingCriteria(requirement, testCases)` starts with every acceptance criterion of a requirement and removes each one that a test case covers according to the chosen matcher. Whatever is left is a coverage gap.

Because every layer is an interface, the matching strategy can be swapped without changing the analyzer, and each layer can be unit-tested with a simple lambda in place of a real AI model.

## Example

```java
Requirement requirement = new Requirement(
        "QE-101",
        "Customer can make a payment",
        List.of("Valid payment succeeds",
                "Invalid routing number is rejected",
                "Confirmation is generated"));

List<TestCase> testCases = List.of(
        new TestCase("TC-001", "Successful payment", List.of("Valid payment succeeds")),
        new TestCase("TC-002", "Reject invalid routing number", List.of("Invalid routing number is rejected")));

CoverageAnalyzer analyzer = new CoverageAnalyzer(new NormalizedCriteriaMatcher());

analyzer.findMissingCriteria(requirement, testCases);
// -> ["Confirmation is generated"]
```

To use an AI model instead, wrap a client in the semantic layers:

```java
CoverageAnalyzer analyzer = new CoverageAnalyzer(
        new SemanticCriteriaMatcher(
                new AiSemanticSimilarityService(new OllamaSemanticAiClient())));
```

## Why multiple matchers

| Requirement criterion | Test criterion | Normalized | Keyword heuristic | LLM |
|---|---|---|---|---|
| Invalid routing number is rejected | `  INVALID routing number   is rejected ` | ✅ match | ✅ match | ✅ match |
| Invalid routing number is rejected | Payment fails when routing number is invalid | ❌ miss | ✅ match | ✅ expected match |
| Customer payment is approved | Customer payment is rejected | ❌ | ❌ (opposite-meaning guard) | ❌ expected |
| Customer payment is approved | Customer account is approved | ❌ | ⚠️ false match (75% word overlap) | ❌ expected |

Exact matching is predictable but misses paraphrases. Keyword overlap catches some paraphrases, but it needs hand-written guards for opposite meanings and still confuses statements that share most of their words while describing different business objects. An LLM handles both cases, at the cost of being non-deterministic, which is why its results are checked by tests rather than trusted blindly.

**Known limitation, captured as a characterization test:** `SimpleSemanticSimilarityServiceTest.knownLimitation_keywordHeuristicTreatsDifferentBusinessObjectsAsEquivalent` asserts the heuristic's current behavior: it wrongly treats a *payment* approval and an *account* approval as the same criterion. The test stays green so the build stays clean, but it fails if the behavior changes. The AI-based matchers are the intended fix, and the planned benchmark will measure this as a precision problem.

## Project structure

```
src/main/java/com/agenticqe/
  analyzer/   CoverageAnalyzer
  matcher/    CriteriaMatcher, SemanticSimilarityService and SemanticAiClient implementations
  model/      Requirement and TestCase records
src/test/java/com/agenticqe/
  analyzer/   CoverageAnalyzerTest
  matcher/    unit tests and an Ollama live test
```

## Running the tests

Requirements: Java 21 and Maven.

```bash
mvn test
```

`OllamaSemanticAiClientLiveTest` calls a real local model. It needs [Ollama](https://ollama.com) running with the model pulled:

```bash
ollama pull qwen2.5:3b
ollama serve
```

All other tests run without any AI model, using lambdas in place of the AI client. All 10 pass, including the characterization test for the known limitation above.

To run only the tests that don't need Ollama:

```bash
mvn test -Dtest='!OllamaSemanticAiClientLiveTest'
```

## Roadmap

- [ ] Claude implementation of `SemanticAiClient` using the official Anthropic Java SDK, with the API key read from the `ANTHROPIC_API_KEY` environment variable
- [ ] Golden-dataset benchmark that scores each matcher on precision and recall
- [ ] Skip live AI tests automatically when the model or API key is not available
- [ ] Evidence-based release validation: verify API, database, messaging and UI evidence before returning PASS, FAIL or INCOMPLETE
- [ ] Browser validation proof of concept with Playwright and MCP
