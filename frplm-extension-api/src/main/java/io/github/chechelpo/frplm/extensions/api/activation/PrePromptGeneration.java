package io.github.chechelpo.frplm.extensions.api.activation;


import io.github.chechelpo.frplm.extensions.api.session.Session;
import io.github.chechelpo.frplm.extensions.api.prompts.PromptBuilder;

public interface PrePromptGeneration {
    /**
     * Runs after the engine has assembled the initial prompt draft but before
     * lorebook outlet resolution, entry activation, and provider dispatch.
     *
     * <p>Implementations may mutate the supplied PromptBuilder. They must not
     * retain the Session, PromptBuilder, or snapshot objects after this call.</p>
     * @since 0.1.0
     */
    void run(Session ofSession, PromptBuilder prompt);
}
