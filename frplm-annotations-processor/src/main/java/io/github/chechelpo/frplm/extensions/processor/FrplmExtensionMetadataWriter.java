package io.github.chechelpo.frplm.extensions.processor;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

final class FrplmExtensionMetadataWriter {
    private static final String EXTENSION_SERVICE_FILE =
            "META-INF/services/io.github.chechelpo.frplm.extensions.api.types.Extension";

    private static final String DESCRIPTOR_DIR =
            "META-INF/frplm/extensions/";

    private static final String SDK_VERSION_RESOURCE =
            "/META-INF/frplm/extension-sdk.properties";

    void write(Filer filer, Set<String> providers) throws IOException {
        String sdkVersion = resolveSdkVersion();

        writeServiceFile(filer, providers);
        writeDescriptorFiles(filer, providers, sdkVersion);
    }

    private void writeServiceFile(Filer filer, Set<String> providers) throws IOException {
        FileObject file = filer.createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                EXTENSION_SERVICE_FILE
        );

        try (Writer writer = file.openWriter()) {
            for (String provider : providers) {
                writer.write(provider);
                writer.write(System.lineSeparator());
            }
        }
    }

    private void writeDescriptorFiles(
            Filer filer,
            Set<String> providers,
            String sdkVersion
    ) throws IOException {
        for (String provider : providers) {
            String path = DESCRIPTOR_DIR + provider + ".properties";

            FileObject file = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    path
            );

            try (Writer writer = file.openWriter()) {
                writer.write("extensionClass=");
                writer.write(provider);
                writer.write(System.lineSeparator());

                writer.write("compiledSdkVersion=");
                writer.write(sdkVersion);
                writer.write(System.lineSeparator());

                writer.write("processorVersion=");
                writer.write(sdkVersion);
                writer.write(System.lineSeparator());
            }
        }
    }

    private String resolveSdkVersion() {
        try (var input = FrplmExtensionMetadataWriter.class.getResourceAsStream(SDK_VERSION_RESOURCE)) {
            if (input == null) {
                return "unknown";
            }

            var properties = new Properties();
            properties.load(input);

            return properties.getProperty("sdk.version", "unknown");
        } catch (IOException ignored) {
            return "unknown";
        }
    }
}