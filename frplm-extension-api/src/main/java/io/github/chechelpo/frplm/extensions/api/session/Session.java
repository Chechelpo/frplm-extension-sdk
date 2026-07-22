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
    /**@return this session's name*/
    @NotNull String getName();
    /**
     * @return user character of this session
     * @apiNote A world assigned to a session won't ever change (Its data might)
     */
    SessionCharacter getUserCharacter();
    /**
     * @return world assigned to this session
     * @apiNote A world assigned to a session won't ever change (Its data might)
     */
    SessionWorld getWorld();
    /**
     * @return the assigned session prompt or empty if its unassigned
     * @apiNote this might be changed by user so don't hold on to it
     */
    Optional<SessionPrompt> getPrompt();
    /** @return current session tick */
    int getCurrentTick();
    /**
     * @param filterEnabled if true, it will filter out disabled messages
     * @return all messages in this session
     */
    @UnmodifiableView
    @NotNull List<ChatMessage> getChatHistory(boolean filterEnabled);
    /**
     * @param filterEnabled if true, all messages that are disabled won't be returned
     * @param number the number of messages to return. The returned list size is guaranteed to be equal or smaller to this number
     * @return n messages of this session, ordered from oldest to younger
     */
    @UnmodifiableView
    @NotNull List<ChatMessage> getLastMessages(int number, boolean filterEnabled);
    /**
     * @param from messages of a tick smaller than this will be excluded
     * @param to messages with a tick larget than this will be excluded
     * @param filterEnabled if true, all messages that are disabled won't be returned
     * @return a list of the <b>last</b> chat messages who pass from <= message_tick <= to. Ordered from oldest to younger
     */
    List<ChatMessage> getLastMessagesRange(int from, int to);
    /**
     * @param isEnabled if true, it will return the last enabled message instead of the last message.
     * @return the last message of this session
     */
    ChatMessage getLastMessage(boolean isEnabled);
}
