package io.github.chechelpo.frplm.extensions.api.utils.openai_compatible;

public record ChatChoice(
            ChatCompletionMessage message,
            String finish_reason
    ) {}