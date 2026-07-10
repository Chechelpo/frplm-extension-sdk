package io.github.chechelpo.frplm.extensions.api.prompts;

import io.github.chechelpo.frplm.extensions.api.standalone.EntrySnapshot;
import io.github.chechelpo.frplm.extensions.api.standalone.LorebookSnapshot;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionRequest;

import java.util.List;
import java.util.Map;

public interface NewMessagePrompt {
    ChatCompletionRequest request();
    Map<LorebookSnapshot, List<EntrySnapshot>> activeEntries();
}
