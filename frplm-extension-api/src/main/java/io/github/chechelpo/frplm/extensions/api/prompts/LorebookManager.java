package io.github.chechelpo.frplm.extensions.api.prompts;

import io.github.chechelpo.frplm.extensions.api.standalone.EntrySnapshot;
import io.github.chechelpo.frplm.extensions.api.standalone.LorebookSnapshot;

import java.util.List;

public interface LorebookManager {
    void addLorebook(LorebookSnapshot lorebook);
    void addLorebooks(List<LorebookSnapshot> lorebooks);

    List<LorebookSnapshot> activeLorebooks();
    boolean entryIsActive(EntrySnapshot.Reference entryReference);
    boolean containsLorebook(LorebookSnapshot lorebook);
}
