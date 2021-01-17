package org.kitteh.vanish;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.Callable;

public final class VanishCheck implements Callable<Object> {
    private final VanishManager manager;
    private final String name;

    public VanishCheck(@NonNull VanishManager manager, @NonNull String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public @NonNull Object call() {
        try {
            return this.manager.isVanished(this.name);
        } catch (final Exception e) {
            return false;
        }
    }
}