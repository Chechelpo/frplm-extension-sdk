package io.github.chechelpo.frplm.extensions.api.session;

import io.github.chechelpo.frplm.extensions.api.results.MoveResult;
import io.github.chechelpo.frplm.extensions.api.standalone.CharacterSnapshot;

public interface SessionCharacter extends CharacterSnapshot {
    boolean isUserCharacter();
    SessionLocation getCurrentLocation();
    MoveResult moveTo(SessionLocation location);
}
