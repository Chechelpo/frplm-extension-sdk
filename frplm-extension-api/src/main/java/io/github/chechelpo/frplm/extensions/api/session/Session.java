package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.standalone.Snapshot;
import io.github.chechelpo.frplm.extensions.api.standalone.StableReference;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Optional;

@Ephemeral
public non-sealed interface Session extends Snapshot<Session.Reference> {
    record Reference(int id) implements StableReference {
        private static final String PREFIX = EntityConfigs.Types.SESSIONS.getEntityType();

        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, id);
        }

        @Contract("_ -> new")
        public static @NotNull Session.Reference fromString(@NotNull String value){
            return ReferenceCodec.parseOne(value, PREFIX, Session.Reference::new);
        }
    }

    SessionCharacter getUserCharacter();
    SessionWorld getWorld();
    Optional<SessionPrompt> getPrompt();

    @UnmodifiableView
    List<ChatMessage> getChatHistory();
    List<ChatMessage> getLastMessages(int number);
    List<ChatMessage> getMessageRange(int from, int to);
    ChatMessage getLastMessage();

}
