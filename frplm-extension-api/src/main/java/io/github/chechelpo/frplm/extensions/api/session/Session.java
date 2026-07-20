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

    int getCurrentTick();
    /** @return all messages in this session */
    @UnmodifiableView
    List<ChatMessage> getChatHistory();
    /** @return the last n messages of this session */
    List<ChatMessage> getLastMessages(int number);
    /** @return a list of the <b>last</b> chat messages who pass from <= message_tick <= to */
    List<ChatMessage> getLastMessagesRange(int from, int to);
    /**@return the last message of this session */
    ChatMessage getLastMessage();
}
