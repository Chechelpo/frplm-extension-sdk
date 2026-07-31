package io.github.chechelpo.frplm.extensions.api.standalone;

import io.github.chechelpo.frplm.extensions.api.session.ChatMessage;
import io.github.chechelpo.frplm.extensions.api.session.Session;
import org.jetbrains.annotations.Nullable;

/**
 * A point-in-time immutable view of engine state.
 *
 * <p>Snapshot objects are valid only for the callback invocation, repository
 * query, or session tick that produced them. Extensions must not retain them
 * in fields, caches, static variables, serialized config, databases, or
 * background tasks.</p>
 *
 * <p>Store the corresponding {@code *Ref} instead and resolve it again through
 * the engine API when needed.</p>
 */
public sealed interface Snapshot <T extends StableReference>
        permits ChatMessage, Session, CharacterSnapshot, ConnectionSnapshot, EntrySnapshot, LocationSnapshot, LorebookSnapshot, PromptSectionEntitySnapshot, PromptSnapshot, RegionSnapshot, TagSnapshot, WorldSnapshot
{
    T asReference();

    default boolean sameEntityAs(@Nullable Snapshot<?> other){
        return other != null && this.asReference().equals(other.asReference());
    }

    default boolean sameEntityAs(@Nullable StableReference other){
        return other != null && this.asReference().equals(other);
    }
}
