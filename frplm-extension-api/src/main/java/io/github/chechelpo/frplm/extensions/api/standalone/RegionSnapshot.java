package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.ReferenceCodec;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Ephemeral
public non-sealed interface RegionSnapshot extends Snapshot<RegionSnapshot.Reference> {
    record Reference(int worldId, int regionId) implements StableReference{
        private static final String PREFIX = EntityConfigs.Types.REGIONS.getEntityType();
        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return ReferenceCodec.encode(PREFIX, worldId, regionId);
        }

        @Contract("_ -> new")
        public static @NotNull RegionSnapshot.Reference fromString(@NotNull String value) {
            return ReferenceCodec.parseTwo(value, PREFIX, RegionSnapshot.Reference::new);
        }
    }

    String getDescription();
    String getName();
    WorldSnapshot getWorld();

    LorebookSnapshot lorebook();
    List<LocationSnapshot> getChildrenLocations();

    List<RegionSnapshot> getChildRegions();
    Optional<RegionSnapshot> parent();
}
