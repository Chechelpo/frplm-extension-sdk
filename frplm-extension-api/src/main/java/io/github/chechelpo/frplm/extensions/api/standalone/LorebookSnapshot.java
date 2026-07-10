package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Ephemeral
public non-sealed interface LorebookSnapshot extends Snapshot<LorebookSnapshot.Reference> {
    record Reference(int id) implements StableReference{
        private static final String PREFIX = EntityConfigs.Types.LOREBOOKS.getEntityType();

        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, this.id);
        }

        @Contract("_ -> new")
        public static @NotNull LorebookSnapshot.Reference fromString(@NotNull String value){
            return ReferenceCodec.parseOne(value, PREFIX, LorebookSnapshot.Reference::new);
        }
    }

    String getName();
    EntrySnapshot[] getEntries();
    default boolean equals(@NotNull LorebookSnapshot other){
        return this.asReference().id() == other.asReference().id();
    }
}
