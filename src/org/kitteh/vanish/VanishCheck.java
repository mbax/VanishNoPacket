package org.kitteh.vanish;

import java.util.concurrent.Callable;

public class VanishCheck implements Callable<Object> {

    private final String name;
    private final VanishManager manager;

    public VanishCheck(VanishManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public Object call() throws Exception {
        return this.manager.isVanished(this.name);
    }

}
