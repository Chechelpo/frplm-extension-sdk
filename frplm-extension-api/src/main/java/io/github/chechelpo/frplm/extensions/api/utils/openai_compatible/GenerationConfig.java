package io.github.chechelpo.frplm.extensions.api.utils.openai_compatible;

public record GenerationConfig(
            boolean streaming,
            boolean exclude_reasoning,
            Integer max_tokens,
            ReasoningEffort reasoning_effort
    ){}
