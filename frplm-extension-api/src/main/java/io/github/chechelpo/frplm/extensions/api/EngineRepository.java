package io.github.chechelpo.frplm.extensions.api;

import io.github.chechelpo.frplm.extensions.api.standalone.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

public interface EngineRepository {
    <T> Optional<?> get(
            @NotNull Class<T> type,
            @NotNull String reference
    );

    @Unmodifiable
    @NotNull List<CharacterSnapshot> getCharacters();
    Optional<CharacterSnapshot> getCharacterWithName(String name);
    Optional<CharacterSnapshot> getCharacter(CharacterSnapshot.Reference reference);

    @Unmodifiable
    @NotNull List<ConnectionSnapshot> getConnections();
    Optional<ConnectionSnapshot> getConnectionWithName(String name);
    Optional<ConnectionSnapshot> getConnection(ConnectionSnapshot.Reference reference);

    @Unmodifiable
    @NotNull List<WorldSnapshot> getWorlds();
    Optional<WorldSnapshot> getWorldWithName(String name);
    Optional<WorldSnapshot> getWorld(WorldSnapshot.Reference reference);

    @Unmodifiable
    @NotNull List<PromptSnapshot> getPrompts();
    Optional<PromptSnapshot> getPromptWithName(String name);
    Optional<PromptSnapshot> getPrompt(PromptSnapshot.Reference reference);

    List<LorebookSnapshot> getLorebooks();
    Optional<LorebookSnapshot> getLorebookWithName(String name);
    Optional<LorebookSnapshot> getLorebook(LorebookSnapshot.Reference reference);

    List<RegionSnapshot> getRegions();
    Optional<RegionSnapshot> getRegion(RegionSnapshot.Reference reference);

    List<LocationSnapshot> getLocations();
    Optional<LocationSnapshot> getLocation(LocationSnapshot.Reference reference);

    List<EntrySnapshot> getEntries();
    Optional<EntrySnapshot> getEntry(EntrySnapshot.Reference reference);

    List<PromptSectionEntitySnapshot> getPromptSections();
    Optional<PromptSectionEntitySnapshot> getPromptSection(PromptSectionEntitySnapshot.Reference reference);

}
