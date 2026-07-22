package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionRequest;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Ephemeral
public non-sealed interface ConnectionSnapshot extends Snapshot<ConnectionSnapshot.Reference> {
    record Reference(int id) implements StableReference {
        private static final String prefix = EntityConfigs.Types.LLM_CONNECTION.getEntityType();

        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return prefix + id;
        }

        @Contract("_ -> new")
        public static @NotNull ConnectionSnapshot.Reference fromString(@NotNull String value){
            if (!value.startsWith(prefix)) throw new IllegalArgumentException("Does not start with " + prefix);
            String raw = value.substring(prefix.length());

            try{
                return new ConnectionSnapshot.Reference(Integer.parseInt(raw));
            }catch(NumberFormatException e){
                throw new IllegalArgumentException("Does not parse " + raw);
            }
        }
    }
    ConnectionSnapshot.Reference asReference();

    boolean hasApiKey();
    int tokenCount(String text);
    String getName();
    Optional<ChatCompletionResponse> generate(ChatCompletionRequest request);
    /**@apiNote validation must be made by caller */
    Optional<ChatCompletionResponse> generate(String rawRequest);
    String getModelID();
}
