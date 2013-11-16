package org.kitteh.vanish;

import java.util.concurrent.Callable;

public final class VanishCheck implements Callable<Object> {
    private final VanishManager manager;
    private final String name;

    public VanishCheck(VanishManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public Object call() {
        try {
            return this.manager.isVanished(this.name);
        } catch (final Exception e) {
            return false;
        }
    }
}