package io.github.chechelpo.frplm.extensions.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FrplmExtensionProcessorTest {
    private static final String SERVICE_FILE =
            "META-INF/services/io.github.chechelpo.frplm.extensions.api.types.Extension";

    private static final String DESCRIPTOR_FILE =
            "META-INF/frplm/extensions/test.extensions.TestExtension.properties";

    @TempDir
    Path tempDir;

    @Test
    void processorGeneratesExtensionServiceDescriptorAndFrplmDescriptor() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        assertNotNull(
                compiler,
                "JDK compiler is unavailable. Run tests with a JDK, not a JRE."
        );

        Path sourceDir = tempDir.resolve("src");
        Path classesDir = tempDir.resolve("classes");

        Files.createDirectories(sourceDir);
        Files.createDirectories(classesDir);

        Path sourceFile = sourceDir.resolve("TestExtension.java");

        Files.writeString(
                sourceFile,
                """
                package test.extensions;

                import io.github.chechelpo.frplm.extensions.api.annotations.FrplmExtension;
                import io.github.chechelpo.frplm.extensions.api.types.Extension;

                @FrplmExtension
                public final class TestExtension extends Extension {
                    public TestExtension() {
                        super(
                            "test-extension",
                            "Test extension",
                            "Test extension description",
                            "https://github.com/chechelpo/frplm"
                        );
                    }
                }
                """,
                StandardCharsets.UTF_8
        );

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {

            fileManager.setLocationFromPaths(
                    StandardLocation.CLASS_OUTPUT,
                    List.of(classesDir)
            );

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromPaths(List.of(sourceFile));

            List<String> options = List.of(
                    "-classpath",
                    System.getProperty("java.class.path"),
                    "-proc:only"
            );

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    options,
                    null,
                    compilationUnits
            );

            task.setProcessors(List.of(new FrplmExtensionProcessor()));

            Boolean success = task.call();

            assertEquals(
                    Boolean.TRUE,
                    success,
                    () -> diagnostics.getDiagnostics()
                            .stream()
                            .map(FrplmExtensionProcessorTest::formatDiagnostic)
                            .collect(Collectors.joining(System.lineSeparator()))
            );
        }

        assertGeneratedServiceFile(classesDir);
        assertGeneratedDescriptorFile(classesDir);
    }

    private static void assertGeneratedServiceFile(Path classesDir) throws Exception {
        Path generatedServiceFile = classesDir.resolve(SERVICE_FILE);

        assertTrue(
                Files.exists(generatedServiceFile),
                "Expected generated service file: " + generatedServiceFile
        );

        String content = Files.readString(generatedServiceFile, StandardCharsets.UTF_8);

        assertEquals(
                "test.extensions.TestExtension" + System.lineSeparator(),
                content
        );
    }

    private static void assertGeneratedDescriptorFile(Path classesDir) throws Exception {
        Path generatedDescriptorFile = classesDir.resolve(DESCRIPTOR_FILE);

        assertTrue(
                Files.exists(generatedDescriptorFile),
                "Expected generated FRPLM descriptor file: " + generatedDescriptorFile
        );

        String content = Files.readString(generatedDescriptorFile, StandardCharsets.UTF_8);

        assertTrue(
                content.contains("extensionClass=test.extensions.TestExtension"),
                content
        );

        assertTrue(
                content.contains("compiledSdkVersion="),
                content
        );

        assertTrue(
                content.contains("processorVersion="),
                content
        );
    }

    private static String formatDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        return diagnostic.getKind()
                + " at line "
                + diagnostic.getLineNumber()
                + ": "
                + diagnostic.getMessage(Locale.ROOT);
    }
}