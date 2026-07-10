module chechelpo.frplm.extensions.api {
    exports io.github.chechelpo.frplm.extensions.api.types;
    exports io.github.chechelpo.frplm.extensions.api.session;
    exports io.github.chechelpo.frplm.extensions.api.results;
    exports io.github.chechelpo.frplm.extensions.api.standalone;
    exports io.github.chechelpo.frplm.extensions.api.utils;
    exports io.github.chechelpo.frplm.extensions.api;
    exports io.github.chechelpo.frplm.extensions.api.prompts;

    
    requires org.jetbrains.annotations;
    requires tools.jackson.databind;
    requires java.logging;
    requires org.slf4j;
}