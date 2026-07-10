package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.annotations.Ephemeral;
import io.github.chechelpo.frplm.extensions.api.standalone.LocationSnapshot;
import org.jetbrains.annotations.NotNull;

@Ephemeral
public interface SessionLocation extends LocationSnapshot {
    SessionCharacter @NotNull [] getCharactersHere();
    SessionLocation @NotNull [] getSessionNeighbours();
}
