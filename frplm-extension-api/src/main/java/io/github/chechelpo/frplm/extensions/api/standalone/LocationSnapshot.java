package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.utils.EntityConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Ephemeral
public non-sealed interface LocationSnapshot extends Snapshot<LocationSnapshot.Reference> {
    record Reference(int worldId, int id) implements StableReference {
        private static final String prefix = EntityConfigs.Types.LOCATIONS.getEntityType();
        @Contract(pure = true)
        @Override
        public @NotNull String encode() {
            return prefix + worldId + "," + id;
        }


        @Contract("_ -> new")
        public static @NotNull Reference fromString(@NotNull String value) {
            if (!value.startsWith(prefix)) {
                throw new IllegalArgumentException("Does not start with " + prefix);
            }

            String raw = value.substring(prefix.length());
            String[] parts = raw.split(",", -1);

            if (parts.length != 2) {
                throw new IllegalArgumentException("Expected format " + prefix + "<worldId>,<id>");
            }

            try {
                int worldId = Integer.parseInt(parts[0].trim());
                int entryId = Integer.parseInt(parts[1].trim());

                return new Reference(worldId, entryId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Could not parse entry reference: " + value, e);
            }
        }
    }
    record Edge<T extends LocationSnapshot>(
                T toLocation,
                String edgeDescription,
                boolean traversable,
                boolean show_destination_name,
                boolean show_destination_description
    ){}

    LorebookSnapshot lorebook();
    LocationSnapshot[] getNeighbours();
    List<Edge<LocationSnapshot>> getOutEdges();
    RegionSnapshot getParentRegion();
    String getName();
    String getDescription();
}
