package io.github.chechelpo.frplm.extensions.api.utils.openai_compatible;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class ModelReasoningMappers {

    private static final Map<ReasoningEffort, String> DEEPSEEK = Map.of(
            ReasoningEffort.None, "none",

            // DeepSeek only exposes high/max as enabled effort levels.
            ReasoningEffort.Minimal, "high",
            ReasoningEffort.Low, "high",
            ReasoningEffort.Medium, "high",
            ReasoningEffort.High, "high",

            ReasoningEffort.Maximum, "max"
    );

    private static final Map<ReasoningEffort, String> STANDARD = Map.of(
            ReasoningEffort.None, "none",
            ReasoningEffort.Minimal, "minimal",
            ReasoningEffort.Low, "low",
            ReasoningEffort.Medium, "medium",
            ReasoningEffort.High, "high",
            ReasoningEffort.Maximum, "xhigh"
    );

    /**
     * Ordered from most specific to least specific.
     *
     * Add exact model exceptions before family rules.
     */
    private static final List<Rule> RULES = List.of(
            /*
             * Example exact override:
             *
             * Rule.exact(
             *     "some-vendor/special-model",
             *     Map.of(
             *         ReasoningEffort.None, "none",
             *         ReasoningEffort.Maximum, "maximum"
             *     )
             * ),
             */

            Rule.prefix("deepseek/", DEEPSEEK)

            /*
             * Do not add STANDARD as a universal fallback unless every
             * unregistered model is known to accept that vocabulary.
             */
    );

    private ModelReasoningMappers() {
    }

    public static @NotNull Optional<String> resolve(
            @NotNull String modelId,
            @NotNull ReasoningEffort effort
    ) {
        Objects.requireNonNull(modelId, "modelId must not be null");
        Objects.requireNonNull(effort, "effort must not be null");

        String normalizedModelId = normalize(modelId);

        return RULES.stream()
                .filter(rule -> rule.matches(normalizedModelId))
                .findFirst()
                .map(Rule::mappings)
                .map(mapping -> mapping.get(effort));
    }

    public static @NotNull String resolveRequired(
            @NotNull String modelId,
            @NotNull ReasoningEffort effort
    ) {
        return resolve(modelId, effort)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No reasoning-effort mapping for model '%s' and effort '%s'"
                                .formatted(modelId, effort)
                ));
    }

    public static boolean supports(
            @NotNull String modelId,
            @NotNull ReasoningEffort effort
    ) {
        return resolve(modelId, effort).isPresent();
    }

    private static @NotNull String normalize(@NotNull String modelId) {
        String normalized = modelId.strip();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("modelId must not be blank");
        }

        return normalized;
    }

    private record Rule(
            Predicate<String> matcher,
            Map<ReasoningEffort, String> mappings
    ) {
        private Rule {
            Objects.requireNonNull(matcher, "matcher must not be null");
            mappings = Map.copyOf(
                    Objects.requireNonNull(mappings, "mappings must not be null")
            );

            if (mappings.isEmpty()) {
                throw new IllegalArgumentException(
                        "Reasoning mappings must not be empty"
                );
            }
        }

        boolean matches(String modelId) {
            return matcher.test(modelId);
        }

        static Rule exact(
                String modelId,
                Map<ReasoningEffort, String> mappings
        ) {
            Objects.requireNonNull(modelId, "modelId must not be null");

            return new Rule(
                    candidate -> candidate.equals(modelId)
                            || candidate.startsWith(modelId + ":"),
                    mappings
            );
        }

        static Rule prefix(
                String prefix,
                Map<ReasoningEffort, String> mappings
        ) {
            Objects.requireNonNull(prefix, "prefix must not be null");

            return new Rule(
                    candidate -> candidate.startsWith(prefix),
                    mappings
            );
        }
    }
}