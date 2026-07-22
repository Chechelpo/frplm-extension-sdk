package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.standalone.Snapshot;
import io.github.chechelpo.frplm.extensions.api.standalone.StableReference;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import io.github.chechelpo.frplm.extensions.api.utils.openai_compatible.ChatCompletionMessage;
import org.jetbrains.annotations.NotNull;


public non-sealed interface ChatMessage extends Snapshot<ChatMessage.Reference> {
    record Reference(int sessionId, int tick_num) implements StableReference {
        private static final String PREFIX = EntityConfigs.Types.MESSAGES.getEntityType();

        @Override
        public String encode() {
            return ReferenceCodec.encode(PREFIX, sessionId, tick_num);
        }

        public static ChatMessage.@NotNull Reference fromValue(@NotNull String value){
            return ReferenceCodec.parseTwo(value, PREFIX, ChatMessage.Reference::new);
        }
    }
    boolean isEnabled();
    int getTick();
    SessionLocation getLocation();
    String content();
    ChatCompletionMessage asChatCompletion();
}
