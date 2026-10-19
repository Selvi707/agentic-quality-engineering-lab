package com.agenticqe.model;

import java.util.List;

public record Requirement(
        String id,
        String description,
        List<String> acceptanceCriteria) {
}
