package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.FindResult;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import io.github.chechelpo.frplm.extensions.api.utils.PromptBudget;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.GenerationConfig;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.GenerationParameters;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Ephemeral
public non-sealed interface PromptSnapshot extends Snapshot<PromptSnapshot.Reference> {
    record Reference(int id) implements StableReference{
        private static final String PREFIX = EntityConfigs.Types.PROMPT_TEMPLATES.getEntityType();

        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, id);
        }

        @Contract("_ -> new")
        public static @NotNull PromptSnapshot.Reference fromString(@NotNull String value){
            return ReferenceCodec.parseOne(PREFIX, value, PromptSnapshot.Reference::new);
        }
    }

    FindResult<ConnectionSnapshot, ?, ?> getAssignedConnection();

    GenerationConfig getGenerationConfig();
    GenerationParameters getParameters();
    PromptBudget getBudgetConfig();
    /** @return ordered (ascendant by position) sections of this prompt */
    List<PromptSectionEntitySnapshot> getSections();
}
