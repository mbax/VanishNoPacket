package org.kitteh.vanish.staticaccess;

public class VanishNotLoadedException extends Exception {

    private static final long serialVersionUID = 1L;

    public VanishNotLoadedException() {
        super("VanishNoPacket isn't loaded!");
    }

}
