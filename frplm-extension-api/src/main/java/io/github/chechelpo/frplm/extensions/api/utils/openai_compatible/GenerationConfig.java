package io.github.chechelpo.frplm.extensions.api.utils.openai_compatible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerationConfig(
        @JsonProperty("stream")
        boolean streaming,

        @JsonProperty("max_tokens")
        Integer maxTokens,

        @JsonProperty("reasoning_effort")
        String reasoningEffort,

        @JsonProperty("reasoning")
        ReasoningConfig reasoning
) {
    /*
     * Compatibility constructor preserving calls such as:
     *
     * new GenerationConfig(false, false, 8192, ReasoningEffort.Maximum)
     */
    public GenerationConfig(
            boolean streaming,
            boolean excludeReasoning,
            Integer maxTokens,
            String reasoningEffort
    ) {
        this(
                streaming,
                maxTokens,
                reasoningEffort,
                new ReasoningConfig(excludeReasoning)
        );
    }


    /**
     * Preserves the old accessor semantics for existing Java callers.
     */
    public boolean exclude_reasoning() {
        return reasoning != null && reasoning.exclude();
    }
}