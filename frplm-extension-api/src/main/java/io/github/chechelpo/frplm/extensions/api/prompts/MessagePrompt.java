package io.github.chechelpo.frplm.extensions.api.prompts;

import io.github.chechelpo.frplm.extensions.api.standalone.EntrySnapshot;
import io.github.chechelpo.frplm.extensions.api.standalone.LorebookSnapshot;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionRequest;

public interface MessagePrompt {
    LorebookSnapshot[] usedLorebooks();
    EntrySnapshot[] activatedEntries();
    ChatCompletionRequest renderedRequest();
}
