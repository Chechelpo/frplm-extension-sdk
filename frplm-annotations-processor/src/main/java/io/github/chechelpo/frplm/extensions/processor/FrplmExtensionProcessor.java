package io.github.chechelpo.frplm.extensions.processor;

import io.github.chechelpo.frplm.extensions.api.annotations.FrplmExtension;
import io.github.chechelpo.frplm.extensions.api.types.Extension;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

@SupportedAnnotationTypes("io.github.chechelpo.frplm.extensions.api.annotations.FrplmExtension")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public final class FrplmExtensionProcessor extends AbstractProcessor {
    private static final String SERVICE_FILE =
            "META-INF/services/io.github.chechelpo.frplm.extensions.api.types.Extension";

    private final Set<String> providers = new LinkedHashSet<>();
    private final FrplmExtensionMetadataWriter metadataWriter =
            new FrplmExtensionMetadataWriter();

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv
    ) {
        TypeElement extensionType = processingEnv.getElementUtils()
                .getTypeElement(Extension.class.getCanonicalName());

        if (extensionType == null) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Could not resolve Extension type"
            );
            return true;
        }

        TypeMirror extensionMirror = extensionType.asType();

        for (Element element : roundEnv.getElementsAnnotatedWith(FrplmExtension.class)) {
            if (!(element instanceof TypeElement typeElement)) {
                continue;
            }

            if (!processingEnv.getTypeUtils()
                    .isAssignable(typeElement.asType(), extensionMirror)) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@FrplmExtension can only be used on subclasses of Extension",
                        element
                );
                continue;
            }

            providers.add(typeElement.getQualifiedName().toString());
        }

        if (roundEnv.processingOver() && !providers.isEmpty()) {
            writeMetadata();
        }

        return true;
    }

    private void writeMetadata() {
        try {
            metadataWriter.write(processingEnv.getFiler(), providers);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to generate FRPLM extension metadata: " + e.getMessage()
            );
        }
    }
}