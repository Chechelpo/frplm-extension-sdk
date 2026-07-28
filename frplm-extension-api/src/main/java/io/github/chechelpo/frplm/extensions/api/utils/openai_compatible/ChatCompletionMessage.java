package io.github.chechelpo.frplm.extensions.api.utils.openai_compatible;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionMessage(
        @JsonProperty("role")
        @NotNull ChatCompletionRole role,

        @JsonProperty("reasoning")
        @JsonAlias("reasoning_content")
        @Nullable String reasoning,

        @JsonProperty("content")
        @Nullable String content
) {
    @Contract("_ -> new")
    public static @NotNull ChatCompletionMessage user(
            @NotNull String content
    ) {
        return new ChatCompletionMessage(
                ChatCompletionRole.USER,
                null,
                content
        );
    }

    @Contract("_ -> new")
    public static @NotNull ChatCompletionMessage assistant(
            @NotNull String content
    ) {
        return new ChatCompletionMessage(
                ChatCompletionRole.ASSISTANT,
                null,
                content
        );
    }

    @Contract("_ -> new")
    public static @NotNull ChatCompletionMessage system(
            @NotNull String content
    ) {
        return new ChatCompletionMessage(
                ChatCompletionRole.SYSTEM,
                null,
                content
        );
    }

    @Override
    public @NotNull String toString() {
        return """
                [
                Role: %s
                Reasoning:
                %s
                Content:
                %s
                ]
                """.formatted(
                role,
                reasoning == null ? "<none>" : reasoning,
                content
        );
    }
}