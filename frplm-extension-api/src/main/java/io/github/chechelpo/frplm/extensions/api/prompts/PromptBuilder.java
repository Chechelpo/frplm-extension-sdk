package io.github.chechelpo.frplm.extensions.api.prompts;

import io.github.chechelpo.frplm.extensions.api.session.ChatMessage;
import io.github.chechelpo.frplm.extensions.api.standalone.LorebookSnapshot;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface PromptBuilder {
    @UnmodifiableView
    List<LorebookSnapshot> getLorebooks();

    /** Add a lorebook. Its entries will be injected if their conditions pass */
    PromptBuilder addLorebook(LorebookSnapshot lorebook);
    /** Add lorebooks
     * @see #addLorebook(LorebookSnapshot) 
     */
    PromptBuilder addLorebooks(List<LorebookSnapshot> lorebooks);
    /**
     * Add lorebooks
     * @see #addLorebook(LorebookSnapshot) 
     */
    PromptBuilder addLorebooks(LorebookSnapshot... lorebookSnapshots);

    /**
     * Appends a section-like message.
     *
     * <p>Unlike normal chat messages, appended sections participate in outlet
     * detection and lorebook entry injection during engine rendering.</p>
     */
    PromptBuilder appendAsSection(ChatCompletionMessage section);


    /** Inserts a chat message at this depth. Is not counted as a prompt section. */
    PromptBuilder insertAt(int depth, ChatCompletionMessage message);

    /* This function is currently unsupported, will add when I do macros instead of just outlets
    /**
     * @param id outlet id
     * @param content to inject
     * @apiNote This will only inject it IF this outlet is actually detected in the prompt. Makes no guarantees as to in
     * which position it will be injected relative to other content which also subscribes to the outlet.
     */
    /*PromptBuilder injectAtOutlet(String outlet, String content);*/
}