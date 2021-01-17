package org.kitteh.vanish;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Logger;

public final class Debuggle {
    private final Logger logger;
    private static Debuggle instance = null;

    public static void log(@NonNull String message) {
        if (Debuggle.instance != null) {
            Debuggle.instance.logger.info("[DEBUG] " + message);
        }
    }

    static void itsGoTime(@NonNull VanishPlugin plugin) {
        Debuggle.instance = new Debuggle(plugin);
    }

    static void nah() {
        Debuggle.instance = null;
    }

    private Debuggle(@NonNull VanishPlugin plugin) {
        this.logger = plugin.getLogger();
        this.logger.info("Debug enabled. Disable in config.yml");
    }
}