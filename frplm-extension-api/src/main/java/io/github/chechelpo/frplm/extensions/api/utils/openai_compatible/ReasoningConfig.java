package io.github.chechelpo.frplm.extensions.api.utils.openai_compatible;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReasoningConfig(
        @JsonProperty("exclude")
        boolean exclude
) {}