package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Ephemeral
public non-sealed interface EntrySnapshot extends Snapshot<EntrySnapshot.Reference>{
    record Reference(int lorebookId, int entryId) implements StableReference {
        private static final String PREFIX = EntityConfigs.Types.ENTRIES.getEntityType();

        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, this.lorebookId, this.entryId);
        }

        @Contract("_ -> new")
        public static @NotNull EntrySnapshot.Reference fromString(@NotNull String value) {
            return ReferenceCodec.parseTwo(value, PREFIX, EntrySnapshot.Reference::new);
        }
    }
}
