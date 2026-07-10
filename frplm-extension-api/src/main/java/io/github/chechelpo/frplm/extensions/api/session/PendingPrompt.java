package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.standalone.LorebookSnapshot;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionMessage;

public interface PendingPrompt {
    boolean addSection(ChatCompletionMessage message, int atDepth);
    LorebookSnapshot[] getLorebooks();
}
