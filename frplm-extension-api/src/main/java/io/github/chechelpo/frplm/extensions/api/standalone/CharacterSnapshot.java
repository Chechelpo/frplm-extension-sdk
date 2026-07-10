package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Ephemeral
public non-sealed interface CharacterSnapshot extends Snapshot<CharacterSnapshot.Reference> {
    record Reference(int id) implements StableReference {
        private static final String PREFIX = EntityConfigs.Types.CHARACTER.getEntityType();

        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, this.id);
        }

        @Contract("_ -> new")
        public static @NotNull CharacterSnapshot.Reference fromString(@NotNull String value){
            return ReferenceCodec.parseOne(value, PREFIX, CharacterSnapshot.Reference::new);
        }
    }

    LorebookSnapshot lorebook();
    String getName();
    LocationSnapshot[] getStartingLocations();
}
