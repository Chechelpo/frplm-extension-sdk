package io.github.chechelpo.frplm.extensions.api.standalone;


import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.prompts.PromptSection;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionMessage;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionRole;
import org.jetbrains.annotations.NotNull;

@Ephemeral
public non-sealed interface PromptSectionEntitySnapshot extends Snapshot<PromptSectionEntitySnapshot.Reference> {
    record Reference(int promptId, int sectionId) implements StableReference {
        private static final String PREFIX = EntityConfigs.Types.SECTIONS.getEntityType();

        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, promptId, sectionId);
        }

        public static @NotNull PromptSectionEntitySnapshot.Reference fromString(String value){
            return ReferenceCodec.parseTwo(value, PREFIX, PromptSectionEntitySnapshot.Reference::new);
        }
    }

    String content();

    int position();
    enum Type{
        CHAT_HISTORY,
        CUSTOM
    }
    PromptSection.InjectAtPosition getInjectionOrder();
    Type type();

    ChatCompletionMessage renderAsCompletionMessage();
    ChatCompletionRole role();
}
