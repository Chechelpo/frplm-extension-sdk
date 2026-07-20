package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.standalone.LocationSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@Ephemeral
public interface SessionLocation extends LocationSnapshot {
    @Unmodifiable
    List<SessionCharacter> getCharactersHere();
    SessionLocation @NotNull [] getSessionNeighbours();
    List<Edge<SessionLocation>> getSessionOutEdges();
}
