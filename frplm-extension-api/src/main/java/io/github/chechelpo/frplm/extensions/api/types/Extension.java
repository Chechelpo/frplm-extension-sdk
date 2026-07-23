package io.github.chechelpo.frplm.extensions.api.types;

import io.github.chechelpo.frplm.extensions.api.EngineRepository;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Extension {
    private final String extensionID;
    private final String name;
    private final String description;
    private final String source;
    private EngineRepository repository;
    private final Logger logger;

    @Contract(pure = true)
    protected Extension(String extensionID, String name , String description, String source){
        Objects.requireNonNull(extensionID, "Extension Id is null");
        Objects.requireNonNull(name, "Extension name is null");
        Objects.requireNonNull(description, "Extension description is null");

        this.extensionID = extensionID;
        this.name = name;
        this.description = description;
        this.source = source;

        this.logger = Logger.getLogger("["+this.name+"]");
    }
    public JsonNode getDefaultConfig(){
        return null;
    }
    protected Logger logger() {
        return this.logger;
    }
    public final void setRepository(EngineRepository repository) {
        if (this.repository != null) throw new IllegalStateException("The repository has already been set");
        this.repository = repository;
    }
    protected final EngineRepository getRepository() {
        return repository;
    }
    public final @NotNull String extensionId(){
        return this.extensionID;
    }
    public final @NotNull String description(){
        return this.description;
    }
    public final @NotNull String displayName(){
        return this.name;
    }
    /** Github repository of the extension for updates */
    @Contract(pure = true)
    public final @NotNull Optional<String> source(){
        return Optional.ofNullable(source);
    }


}
