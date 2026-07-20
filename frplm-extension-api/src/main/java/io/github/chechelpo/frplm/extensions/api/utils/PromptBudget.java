package io.github.chechelpo.frplm.extensions.api.utils;

public record PromptBudget(
        int maxTokens,
        double lorebookRatio,
        double chatRatio
) {
    public PromptBudget {
        if (maxTokens < 0) {
            throw new IllegalArgumentException(
                    "maxTokens must be non-negative"
            );
        }

        if (!Double.isFinite(lorebookRatio)
                || lorebookRatio < 0.0f
                || lorebookRatio > 1.0f) {
            throw new IllegalArgumentException(
                    "lorebookRatio must be between 0 and 1"
            );
        }

        if (!Double.isFinite(chatRatio)
                || chatRatio < 0.0f
                || chatRatio > 1.0f) {
            throw new IllegalArgumentException(
                    "chatRatio must be between 0 and 1"
            );
        }

        if (lorebookRatio + chatRatio > 1.0f) {
            throw new IllegalArgumentException(
                    "lorebookRatio + chatRatio must not exceed 1"
            );
        }
    }
}