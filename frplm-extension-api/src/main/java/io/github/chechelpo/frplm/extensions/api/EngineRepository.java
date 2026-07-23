package io.github.chechelpo.frplm.extensions.api;

import io.github.chechelpo.frplm.extensions.api.standalone.*;
import io.github.chechelpo.frplm.extensions.api.utils.FindResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Optional;

public interface EngineRepository {
    <S extends StableReference, E extends Snapshot<S>> FindResult<E, ?, ?> get(
            Class<E> type,
            S reference
    );


    <E extends Snapshot<?>> FindResult<E, ?, ?> resolve(
            Class<E> type,
            String reference
    );

    <E extends Snapshot<?>> @NotNull @Unmodifiable List<E> getAll(Class<E> ofType);
}
