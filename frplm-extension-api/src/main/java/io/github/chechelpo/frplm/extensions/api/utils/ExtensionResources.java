package io.github.chechelpo.frplm.extensions.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public final class ExtensionResources {
    public static final String EXTENSIONS_ROOT = "extensions";
    public static final String CONFIG_PANEL = "panel.js";

    private final Class<?> anchorClass;
    private final String extensionId;
    private final String classpathRoot;

    public ExtensionResources(
            @NotNull Class<?> anchorClass,
            @NotNull String extensionId
    ) {
        this.anchorClass = Objects.requireNonNull(anchorClass, "anchorClass is null");
        this.extensionId = normalizeExtensionId(
                Objects.requireNonNull(extensionId, "extensionId is null")
        );

        this.classpathRoot = EXTENSIONS_ROOT + "/" + this.extensionId;
    }

    public @NotNull Optional<io.WebAsset> getAsset(@NotNull String relativePath) {
        validateRelativeAssetPath(relativePath);
        return io.getAssetFromClassPath(anchorClass, classpathRoot, relativePath);
    }

    public @NotNull io.WebAsset requireAsset(@NotNull String relativePath) {
        return getAsset(relativePath).orElseThrow(() ->
                new IllegalStateException(
                        "Missing extension asset: " +
                                "/" + classpathRoot + "/" + relativePath
                )
        );
    }

    public @NotNull String assetUrl(@NotNull String relativePath) {
        validateRelativeAssetPath(relativePath);
        return "/extensions/" + extensionId + "/assets/" + relativePath;
    }

    public @NotNull String configPanelUrl() {
        return assetUrl(CONFIG_PANEL);
    }

    public @NotNull String classpathRoot() {
        return classpathRoot;
    }

    private static @NotNull String normalizeExtensionId(@NotNull String extensionId) {
        String normalized = extensionId.strip();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("extensionId must not be blank");
        }

        if (normalized.startsWith("/") || normalized.endsWith("/")) {
            throw new IllegalArgumentException("extensionId must not start or end with '/': " + extensionId);
        }

        if (normalized.contains("/")) {
            throw new IllegalArgumentException("extensionId must not contain '/': " + extensionId);
        }

        if (normalized.contains("..")) {
            throw new IllegalArgumentException("extensionId must not contain '..': " + extensionId);
        }

        return normalized;
    }

    private static void validateRelativeAssetPath(@NotNull String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath is null");

        if (relativePath.isBlank()) {
            throw new IllegalArgumentException("Asset path must not be blank");
        }

        if (relativePath.startsWith("/")) {
            throw new IllegalArgumentException("Asset path must be relative: " + relativePath);
        }

        if (relativePath.contains("..")) {
            throw new IllegalArgumentException("Asset path must not contain '..': " + relativePath);
        }
    }
}