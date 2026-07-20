package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Ephemeral
public non-sealed interface WorldSnapshot extends Snapshot<WorldSnapshot.Reference> {
    record Reference(int id) implements StableReference {
        private static final String PREFIX = "world: ";

        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, id);
        }

        @Contract("_ -> new")
        public static @NotNull WorldSnapshot.Reference fromString(@NotNull String value){
            return ReferenceCodec.parseOne(value, PREFIX, WorldSnapshot.Reference::new);
        }
    }

    String getName();
    String getDescription();
    WorldSnapshot.Reference asReference();
    LorebookSnapshot lorebook();
    LocationSnapshot[] getNeighboursOf(LocationSnapshot loc);
    List<RegionSnapshot> getRootRegions();

    boolean areNeighbours(LocationSnapshot loc, LocationSnapshot other);
}
