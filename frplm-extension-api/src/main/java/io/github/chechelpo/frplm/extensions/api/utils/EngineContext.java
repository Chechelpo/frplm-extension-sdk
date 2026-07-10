package io.github.chechelpo.frplm.extensions.api.utils;

import io.github.chechelpo.frplm.extensions.api.EngineRepository;

public record EngineContext(
        EngineRepository standaloneFactory
) {}
