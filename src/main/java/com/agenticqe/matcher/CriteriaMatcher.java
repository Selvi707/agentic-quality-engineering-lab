package com.agenticqe.matcher;

public interface CriteriaMatcher {
    boolean matches(String requirementCriterion, String testCriterion);
}
