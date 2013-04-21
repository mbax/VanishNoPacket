package org.kitteh.vanish.staticaccess;

/**
 * Our princess is in another castle
 */
public final class VanishNotLoadedException extends Exception {
    private static final long serialVersionUID = 1L;

    public VanishNotLoadedException() {
        super("VanishNoPacket isn't loaded!");
    }
}