package io.github.chechelpo.frplm.extensions.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public final class ReferenceCodec {
    private static final char SEPARATOR = ',';

    private ReferenceCodec() {}

    @Contract(pure = true)
    public static @NotNull String encode(@NotNull String prefix, int id) {
        return prefix + id;
    }

    @Contract(pure = true)
    public static @NotNull String encode(@NotNull String prefix, int first, int second) {
        return prefix + first + SEPARATOR + second;
    }

    @Contract(pure = true)
    public static @NotNull String encode(@NotNull String prefix, int @NotNull ... ids) {
        if (ids.length == 0) {
            throw new IllegalArgumentException("Reference must contain at least one id");
        }

        StringBuilder builder = new StringBuilder(prefix);

        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                builder.append(SEPARATOR);
            }

            builder.append(ids[i]);
        }

        return builder.toString();
    }

    public static int parseOne(@NotNull String value, @NotNull String prefix) {
        return parse(value, prefix, 1)[0];
    }

    public static int @NotNull [] parse(
            @NotNull String value,
            @NotNull String prefix,
            int expectedArity
    ) {
        if (expectedArity <= 0) {
            throw new IllegalArgumentException("Expected arity must be positive");
        }

        if (!value.startsWith(prefix)) {
            throw new IllegalArgumentException(
                    "Reference does not start with expected prefix '" + prefix + "': " + value
            );
        }

        String raw = value.substring(prefix.length());

        if (raw.isEmpty()) {
            throw new IllegalArgumentException(
                    "Reference is missing id payload after prefix '" + prefix + "': " + value
            );
        }

        String[] parts = raw.split(String.valueOf(SEPARATOR), -1);

        if (parts.length != expectedArity) {
            throw new IllegalArgumentException(
                    "Expected " + expectedArity + " id part(s) after prefix '" + prefix +
                            "', but found " + parts.length + ": " + value
            );
        }

        int[] ids = new int[expectedArity];

        for (int i = 0; i < parts.length; i++) {
            ids[i] = parseStrictInt(parts[i], value, i);
        }

        return ids;
    }

    public static <R> @NotNull R parseOne(
            @NotNull String value,
            @NotNull String prefix,
            @NotNull IntFunction<R> constructor
    ) {
        int id = parseOne(value, prefix);
        return constructor.apply(id);
    }

    public static <R> @NotNull R parseTwo(
            @NotNull String value,
            @NotNull String prefix,
            @NotNull IntIntFunction<R> constructor
    ) {
        int[] ids = parse(value, prefix, 2);
        return constructor.apply(ids[0], ids[1]);
    }

    private static int parseStrictInt(
            @NotNull String raw,
            @NotNull String originalValue,
            int index
    ) {
        if (raw.isEmpty()) {
            throw new IllegalArgumentException(
                    "Reference id part " + index + " is empty: " + originalValue
            );
        }

        if (!raw.equals(raw.trim())) {
            throw new IllegalArgumentException(
                    "Reference id part " + index + " contains surrounding whitespace: " + originalValue
            );
        }

        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Reference id part " + index + " is not a valid integer: " + originalValue,
                    e
            );
        }
    }

    @FunctionalInterface
    public interface IntIntFunction<R> {
        R apply(int first, int second);
    }
}