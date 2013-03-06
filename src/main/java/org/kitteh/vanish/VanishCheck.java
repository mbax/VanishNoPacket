package org.kitteh.vanish;

import java.util.concurrent.Callable;

import org.kitteh.vanish.staticaccess.VanishNoPacket;

public class VanishCheck implements Callable<Object> {

    private final String name;

    public VanishCheck(String name) {
        this.name = name;
    }

    @Override
    public Object call() {
        try {
            return VanishNoPacket.isVanished(this.name);
        } catch (final Exception e) {
            return false;
        }
    }
}