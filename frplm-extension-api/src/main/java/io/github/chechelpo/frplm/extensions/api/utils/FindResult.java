package io.github.chechelpo.frplm.extensions.api.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * Represents the outcome of a find operation, explicitly distinguishing between a found state containing a value and a not found state.
 * Provides utility methods for state checking, conditional execution, value extraction, and transformation.
 */
public interface FindResult<
        T,
        N extends FindResult.NotFound<T, N, F>,
        F extends FindResult.Found<T, N, F>
        > {
    static <T> FindResult<T, ?, ?> empty(String message){
        return new FindResult.Mapped.NotFound<>(message);
    }

    default boolean isFound(){
        return this instanceof FindResult.Found<T, N, F>;
    }
    default boolean isNotFound(){
        return this instanceof FindResult.NotFound<T, N, F>;
    }

    /**
     * Executes the given consumer with this instance if this result is a found.
     *
     * @param consumer the consumer to execute if this is a Found result
     * @return this FindResult instance
     */
    @SuppressWarnings("unchecked")
    default FindResult<T, N, F> ifFound(Consumer<? super F> consumer) {
        if (this instanceof FindResult.Found<T, N , F> found)
            consumer.accept((F) this);
        return this;
    }
    /**
     * Executes the given consumer with this instance if this result is not found.
     *
     * @param consumer the consumer to execute if this is a NotFound result
     * @return this FindResult instance
     */
    @SuppressWarnings("unchecked")
    default FindResult<T, N, F> ifNotFound(Consumer<? super N> consumer){
        if (this instanceof FindResult.NotFound<T, N, F> notFound){
            consumer.accept((N) notFound);
        }

        return this;
    }


    // ---- Defaults / Unwrapping ----
    /**
     * Returns the value associated with this found result.
     * @throws NoSuchElementException If this result represents a not found
     * @return the value of this found result
     */
    default T get() throws NoSuchElementException {
        if (this instanceof FindResult.NotFound<T, N, F>) throw new NoSuchElementException("Called get() for a not found");
        return ((Found<T, N, F>) this).value();
    }
    default T orElse(T defaultValue) {
        return isFound() ? ((Found<T,N,F>) this).value() : defaultValue;
    }
    default T orElseGet(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return isFound() ? ((Found<T, N, F>) this).value() : supplier.get();
    }

    default Stream<T> stream() {
        return isFound() ? Stream.of(((Found<T,N,F>) this).value()) : Stream.empty();
    }

    /**
     *
     */
    default <T2> FindResult<T2, Mapped.NotFound<T2>, Mapped.Found<T2>> mapResult(
            Function<? super T, ? extends T2> mapper
    ) {
        Objects.requireNonNull(mapper, "mapper");
        if (this instanceof FindResult.NotFound<T, N, F> notFound) {
            return new Mapped.NotFound<>(notFound.toDebugString());
        }
        T2 mapped = mapper.apply(((Found<T, N, F>) this).value());
        return new Mapped.Found<>(mapped);
    }
    /**
     * A {@link FindResult} produced by {@link #mapResult}, representing the
     * next link in a find chain. It is itself a find result so chains can be
     * composed and queried uniformly.
     */
    sealed interface Mapped<T>
            extends FindResult<T, Mapped.NotFound<T>, Mapped.Found<T>>
            permits Mapped.NotFound, Mapped.Found {

        /**
         * Short-circuited link in the chain: either the upstream result was
         * not found, or the downstream lookup returned not found.
         */
        record NotFound<T>(String source) implements Mapped<T>, FindResult.NotFound<
                T,
                Mapped.NotFound<T>,
                Mapped.Found<T>
                > {
            @Override
            public String toDebugString() {
                return "Chain not found: " + source;
            }

            @Override
            public String toString() {
                return toDebugString();
            }
        }

        /**
         * Resolved link in the chain carrying the value produced by the
         * downstream find result.
         */
        record Found<T>(T value) implements Mapped<T>, FindResult.Found<
                T,
                Mapped.NotFound<T>,
                Mapped.Found<T>
                > {
        }
    }

    /**
     * Applies the given mapping function to the value of this result if it represents a found result, returning an Optional containing the mapped value.
     * If this result represents a not found result, returns an empty Optional.
     *
     * @param mapper the mapping function to apply to the value if this is a found result.
     * @return an Optional containing the mapped value if this is a found result, otherwise an empty Optional
     */
    default <Q> Optional<Q> map(Function<? super T, ? extends Q> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return switch (this) {
            case Found<T,N,F> found -> Optional.ofNullable(mapper.apply(found.value()));
            case NotFound<T, N, F> ignored -> Optional.empty();
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }

    // ---- Throwing ----
    default <X extends Throwable> T orElseThrow(
            Function<NotFound<T, N, F>, ? extends X> exceptionFactory
    ) throws X {
        if (this instanceof NotFound<T, N, F> notFound)
            throw exceptionFactory.apply(notFound);

        return ((Found<T, N, F>) this).value();
    }

    default T orElseThrow(){
        if (this instanceof NotFound<T, N, F> error)
            throw new NullPointerException(error.toDebugString());
        return ((Found<T,N,F>) this).value();
    }
    default T orElseThrow(String message){
        if (this instanceof NotFound<T, N, F> error)
            throw new NullPointerException(message + "\n" + error);

        return ((Found<T,N,F>) this).value();
    }

    interface NotFound<T,
            N extends NotFound<T, N, F>,
            F extends Found<T, N, F>>
            extends FindResult<T, N, F>
    {
        String toDebugString();
    }

    interface Found<T,
            N extends NotFound<T, N, F>,
            F extends Found<T, N, F>>
            extends FindResult<T, N, F>
    {
        T value();
    }
}
