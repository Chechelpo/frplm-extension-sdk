package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.standalone.PromptSnapshot;
import io.github.chechelpo.frplm.extensions.api.prompts.PromptBuilder;

public interface SessionPrompt extends PromptSnapshot {
    PromptBuilder getNewMessagePrompt();

}
