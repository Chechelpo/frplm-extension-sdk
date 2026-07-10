package io.github.chechelpo.frplm.extensions.api.activation;

import io.github.chechelpo.frplm.extensions.api.session.Session;

public interface PostResponseGeneration {
    void onNewGeneration(Session session);
}
