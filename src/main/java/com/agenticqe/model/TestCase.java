package com.agenticqe.model;

import java.util.List;

public record TestCase(
        String id,
        String name,
        List<String> coveredAcceptanceCriteria
) {
}