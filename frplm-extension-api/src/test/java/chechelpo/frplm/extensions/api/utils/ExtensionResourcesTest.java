package chechelpo.frplm.extensions.api.utils;

import io.github.chechelpo.frplm.extensions.api.types.Extension;
import io.github.chechelpo.frplm.extensions.api.utils.ExtensionResources;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

final class ExtensionResourcesTest {

    private static final class TestResourceExtension extends Extension {
        private static final String ID = "test-resource-extension";

        private final ExtensionResources resources;

        private TestResourceExtension() {
            super(
                    ID,
                    "Test Resource Extension",
                    "Extension used to test classpath resources",
                    null
            );

            this.resources = new ExtensionResources(
                    this.getClass(),
                    this.extensionId()
            );
        }

        ExtensionResources resources() {
            return resources;
        }
    }

    @Test
    void usesCanonicalExtensionClasspathRoot() {
        TestResourceExtension extension = new TestResourceExtension();

        assertEquals(
                "extensions/test-resource-extension",
                extension.resources().classpathRoot()
        );
    }

    @Test
    void findsConfigPanelAsset() {
        TestResourceExtension extension = new TestResourceExtension();

        Optional<?> asset = extension.resources().getAsset("panel.js");

        assertTrue(asset.isPresent(), "panel.js should exist in test resources");
    }

    @Test
    void requireAssetReturnsExistingAsset() {
        TestResourceExtension extension = new TestResourceExtension();

        Object asset = extension.resources().requireAsset("panel.js");

        assertNotNull(asset);
    }

    @Test
    void findsNestedAsset() {
        TestResourceExtension extension = new TestResourceExtension();

        Optional<?> asset = extension.resources().getAsset("nested/help.txt");

        assertTrue(asset.isPresent(), "nested/help.txt should exist in test resources");
    }

    @Test
    void missingAssetReturnsEmptyOptional() {
        TestResourceExtension extension = new TestResourceExtension();

        Optional<?> asset = extension.resources().getAsset("missing.js");

        assertTrue(asset.isEmpty(), "missing asset should return Optional.empty()");
    }

    @Test
    void requireAssetThrowsForMissingAsset() {
        TestResourceExtension extension = new TestResourceExtension();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> extension.resources().requireAsset("missing.js")
        );

        assertTrue(exception.getMessage().contains("/extensions/test-resource-extension/missing.js"));
    }

    @Test
    void generatesConfigPanelUrl() {
        TestResourceExtension extension = new TestResourceExtension();

        assertEquals(
                "/extensions/test-resource-extension/assets/panel.js",
                extension.resources().configPanelUrl()
        );
    }

    @Test
    void generatesAssetUrl() {
        TestResourceExtension extension = new TestResourceExtension();

        assertEquals(
                "/extensions/test-resource-extension/assets/style.css",
                extension.resources().assetUrl("style.css")
        );
    }

    @Test
    void rejectsAbsoluteAssetPath() {
        TestResourceExtension extension = new TestResourceExtension();

        assertThrows(
                IllegalArgumentException.class,
                () -> extension.resources().getAsset("/panel.js")
        );
    }

    @Test
    void rejectsPathTraversalAssetPath() {
        TestResourceExtension extension = new TestResourceExtension();

        assertThrows(
                IllegalArgumentException.class,
                () -> extension.resources().getAsset("../panel.js")
        );
    }

    @Test
    void rejectsBlankAssetPath() {
        TestResourceExtension extension = new TestResourceExtension();

        assertThrows(
                IllegalArgumentException.class,
                () -> extension.resources().getAsset(" ")
        );
    }

    @Test
    void rejectsExtensionIdWithSlash() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ExtensionResources(TestResourceExtension.class, "bad/id")
        );
    }

    @Test
    void rejectsBlankExtensionId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ExtensionResources(TestResourceExtension.class, " ")
        );
    }

    @Test
    void trimsExtensionIdWhitespace() {
        ExtensionResources resources = new ExtensionResources(
                TestResourceExtension.class,
                " test-resource-extension "
        );

        assertEquals(
                "extensions/test-resource-extension",
                resources.classpathRoot()
        );

        assertTrue(resources.getAsset("panel.js").isPresent());
    }
}