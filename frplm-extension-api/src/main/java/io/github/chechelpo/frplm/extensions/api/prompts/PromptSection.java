package io.github.chechelpo.frplm.extensions.api.prompts;

public interface PromptSection {
    sealed interface InjectAtPosition permits InjectAtPosition.AtDepth, InjectAtPosition.Relative {
        /** Injected at a certain message depth inside the chat history */
        record AtDepth(int atDepth) implements InjectAtPosition {}
        /** Injected relative to other prompt sections */
        record Relative(int number) implements InjectAtPosition {}
    }

    InjectAtPosition injectInstruction();
    String content();
}
