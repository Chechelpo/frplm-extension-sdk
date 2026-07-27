package io.github.chechelpo.frplm.extensions.api.types;

import io.github.chechelpo.frplm.extensions.api.EngineRepository;
import io.github.chechelpo.frplm.extensions.api.standalone.Snapshot;
import io.github.chechelpo.frplm.extensions.api.utils.ExtensionDBBridge;
import io.github.chechelpo.frplm.extensions.api.utils.ExtensionResources;
import io.github.chechelpo.frplm.extensions.api.utils.FindResult;
import io.github.chechelpo.frplm.extensions.api.utils.io;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @param <T> your configuration data carrier class, serializable by jackson
 */
public abstract class ConfigurableExtension<T> extends Extension {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<T> configClazz;
    private final T defaultConfig;
    private final HashMap<String, FieldConfig> fields = new HashMap<>();
    private final ExtensionResources resources;

    protected ConfigurableExtension(
            String extensionID,
            String name,
            String description,
            String source,
            Class<T> configClazz,
            T defaultConfig
    ) {
        super(extensionID, name, description, source);

        this.defaultConfig = Objects.requireNonNull(defaultConfig, "Default config is null");
        this.configClazz = configClazz;

        this.resources = new ExtensionResources(
                this.getClass(),
                extensionID
        );

        this.resources.requireAsset(ExtensionResources.CONFIG_PANEL);
    }
    @Override
    public JsonNode getDefaultConfig(){
        return OBJECT_MAPPER.valueToTree(defaultConfig);
    }

    public abstract void onConfigChange(T oldConfig, T newConfig);
    public void validateConfig(T config){}

    public final @NotNull Optional<io.WebAsset> getAsset(@NotNull String relativePath) {
        return resources.getAsset(relativePath);
    }
    public final @NotNull io.WebAsset requireAsset(@NotNull String relativePath) {
        return resources.requireAsset(relativePath);
    }

    protected final T getCurrentConfig() {
        requireDBBridge();
        return this.dbBridge.getConfig(this.extensionId(), configClazz);
    }

    protected final void saveConfig(JsonNode config) {
        requireDBBridge();
        this.dbBridge.saveConfig(this.extensionId(), config);
    }


    protected final void setFieldConfig(@NotNull String fieldName, @NotNull FieldConfig field) {
        Objects.requireNonNull(fieldName, "fieldName is null");
        Objects.requireNonNull(field, "field is null");

        fields.put(fieldName, field);
    }

    protected final FieldConfig getFieldConfig(String fieldName) {
        return fields.get(fieldName);
    }

    @Contract(pure = true)
    public final @NotNull @Unmodifiable Map<String, FieldConfig> getFields() {
        return Map.copyOf(fields);
    }

    public final @NotNull T patchConfig(@NotNull JsonNode patch) {
        Objects.requireNonNull(patch, "patch is null");
        requireDBBridge();

        if (!(patch instanceof ObjectNode patchObject)) {
            throw new IllegalArgumentException(
                    "Configuration patch must be a JSON object"
            );
        }

        T previousConfig = getCurrentConfig();

        JsonNode currentTree = OBJECT_MAPPER.valueToTree(previousConfig);

        if (!(currentTree instanceof ObjectNode currentObject)) {
            throw new IllegalStateException(
                    "Configuration type must serialize as a JSON object: "
                            + configClazz.getName()
            );
        }

        ObjectNode mergedConfig = currentObject.deepCopy();
        patchObject.properties().forEach(entry ->
                mergedConfig.set(
                        entry.getKey(),
                        entry.getValue().deepCopy()
                )
        );

        validateFieldConfig(mergedConfig);
        final T candidate;
        try {
            candidate = OBJECT_MAPPER.treeToValue(
                    mergedConfig,
                    configClazz
            );
        } catch (JacksonException exception) {
            String message =
                    "Configuration patch does not produce a valid "
                            + configClazz.getSimpleName();

            logger().severe(message + ": " + mergedConfig);

            throw new IllegalArgumentException(message, exception);
        }

        validateConfig(candidate);
        saveConfig(mergedConfig);
        onConfigChange(previousConfig, candidate);

        return candidate;
    }

    public final @NotNull T replaceConfig(@NotNull JsonNode completeConfig) {
        Objects.requireNonNull(completeConfig, "completeConfig is null");
        requireDBBridge();

        validateFieldConfig(completeConfig);

        final T candidate;
        try {
            candidate = OBJECT_MAPPER.treeToValue(
                    completeConfig,
                    configClazz
            );
        } catch (JacksonException exception) {
            throw new IllegalArgumentException(
                    "Invalid complete configuration",
                    exception
            );
        }

        validateConfig(candidate);

        T previousConfig = getCurrentConfig();

        saveConfig(completeConfig.deepCopy());
        onConfigChange(previousConfig, candidate);

        return candidate;
    }


    private void validateFieldConfig(@NotNull JsonNode newConfig) {
        EngineRepository repository = getRepository();

        if (repository == null) {
            throw new IllegalStateException("Repository has not been set");
        }

        for (String name : fields.keySet()) {
            if (!newConfig.has(name)) {
                continue;
            }

            FieldConfig fieldConfig = getFieldConfig(name);
            JsonNode newValue = newConfig.get(name);

            if (newValue.isNull()) {
                if (!fieldConfig.field.nullable()) {
                    throw new IllegalArgumentException("Field " + name + " has null value");
                }

                continue;
            }

            Optional<String> errorMessage = fieldConfig.field.validate(newValue, repository);

            if (errorMessage.isPresent()) {
                String message = "Field " + name + " has invalid value: " + errorMessage.get();
                this.logger().severe(message);
                throw new IllegalArgumentException(message);
            }
        }
    }


    public record FieldConfig(
            @NotNull String label,
            @Nullable String description,
            @NotNull Field field
    ){}

    public sealed interface Field {
        boolean nullable();
        Optional<String> validate(JsonNode value, EngineRepository repository);

        record SnapshotSelection<T extends Snapshot>(
                boolean nullable,
                int minSelections,
                int maxSelections,
                @NotNull Class<T> type
        ) implements Field {

            public static <T extends Snapshot> SnapshotSelection<T> optionalSingle(
                    @NotNull Class<T> type
            ) {
                return new SnapshotSelection<>(false, 0, 1, type);
            }

            public static <T extends Snapshot> SnapshotSelection<T> requiredSingle(
                    @NotNull Class<T> type
            ) {
                return new SnapshotSelection<>(false, 1, 1, type);
            }

            public static <T extends Snapshot> SnapshotSelection<T> optionalMany(
                    int maxSelections,
                    @NotNull Class<T> type
            ) {
                return new SnapshotSelection<>(false, 0, maxSelections, type);
            }

            public static <T extends Snapshot> SnapshotSelection<T> requiredMany(
                    int minSelections,
                    int maxSelections,
                    @NotNull Class<T> type
            ) {
                return new SnapshotSelection<>(false, minSelections, maxSelections, type);
            }

            public SnapshotSelection {
                if (type == null) {
                    throw new IllegalArgumentException("type must not be null");
                }

                if (minSelections < 0) {
                    throw new IllegalArgumentException("minSelections must be >= 0");
                }

                if (maxSelections < minSelections) {
                    throw new IllegalArgumentException("maxSelections must be >= minSelections");
                }
            }

            @Override
            public Optional<String> validate(
                    @NotNull JsonNode value,
                    @NotNull EngineRepository repository
            ) {
                if (!value.isArray()) {
                    return Optional.of("Expected array of snapshot references");
                }

                int count = value.size();

                if (count < minSelections || count > maxSelections) {
                    return Optional.of(
                            "Invalid selection count: " + count
                                    + " [" + minSelections + ", " + maxSelections + "]"
                    );
                }

                for (JsonNode element : value) {
                    if (!element.isString()) {
                        return Optional.of("Non string snapshot reference: " + element);
                    }

                    String reference = element.asString();

                    if (reference.isBlank()) {
                        return Optional.of("Blank snapshot reference");
                    }

                    FindResult<T, ?, ?> snapshot = repository.resolve(type, reference);

                    if (snapshot.isNotFound()) {
                        return Optional.of(
                                "No entity of class "
                                        + type.getSimpleName()
                                        + " with reference "
                                        + reference
                        );
                    }
                }

                return Optional.empty();
            }
        }

        sealed interface PrimitiveField extends Field {
            record IntegerConfig(boolean nullable, int minValue, int maxValue) implements PrimitiveField {
                public IntegerConfig{
                    if (maxValue < minValue) throw new IllegalArgumentException();
                }

                @Override
                public @NotNull Optional<String> validate(@NotNull JsonNode value, EngineRepository repository){
                    if (!value.isInt()) return Optional.of("Non integer field: " + value.asString());
                    int integerValue = value.asInt();
                    if (integerValue < minValue || integerValue > maxValue)
                        return Optional.of("Out of bounds: " + integerValue + " [" + minValue + ", " + maxValue + "]");

                    return Optional.empty();
                }
            }

            record DoubleConfig(boolean nullable, double minValue, double maxValue) implements PrimitiveField {
                public DoubleConfig {
                    if (Double.isNaN(minValue) || Double.isNaN(maxValue)) {
                        throw new IllegalArgumentException("Bounds must not be NaN");
                    }

                    if (maxValue < minValue) {
                        throw new IllegalArgumentException("maxValue must be >= minValue");
                    }
                }

                @Override
                public Optional<String> validate(
                        @NotNull JsonNode value,
                        @NotNull EngineRepository repository
                ) {
                    if (!value.isNumber()) {
                        return Optional.of("Non numeric field: " + value);
                    }

                    double doubleValue = value.asDouble();

                    if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                        return Optional.of("Non finite double field: " + doubleValue);
                    }

                    if (doubleValue < minValue || doubleValue > maxValue) {
                        return Optional.of(
                                "Out of bounds: " + doubleValue
                                        + " [" + minValue + ", " + maxValue + "]"
                        );
                    }

                    return Optional.empty();
                }
            }

            record StringConfig(boolean nullable, int minLength, int maxLength) implements PrimitiveField {
                public StringConfig{
                    if (minLength < 0) throw new IllegalArgumentException();
                    if (maxLength < minLength) throw new IllegalArgumentException();
                }

                @Override
                public Optional<String> validate(JsonNode value, EngineRepository repository) {
                    if (!value.isString()) return Optional.of("Non string field: " + value.asString());
                    String stringValue = value.asString();
                    if (stringValue.length() < minLength || stringValue.length() > maxLength){
                        return Optional.of("Out of bounds string length: " + stringValue.length() + " [" + minLength + ", " + maxLength + "]");
                    }

                    return Optional.empty();
                }
            }

            record BooleanConfig(boolean nullable) implements PrimitiveField {
                @Override
                public Optional<String> validate(JsonNode value, EngineRepository repository) {
                    if (!value.isBoolean()) return Optional.of("Non boolean field: " + value.asString());
                    return Optional.empty();
                }
            }
        }
    }
}