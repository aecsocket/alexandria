package io.github.aecsocket.alexandria.fabric;

import java.util.Optional;

public interface Persistable {
    default Optional<Boolean> isPersistent() {
        return Optional.empty();
    }

    default void setPersistent(boolean value) {}

    default void clearPersistent() {}
}
