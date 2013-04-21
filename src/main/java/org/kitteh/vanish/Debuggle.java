package org.kitteh.vanish;

import java.util.logging.Logger;

public final class Debuggle {
    private final Logger logger;
    private static Debuggle instance = null;

    public static void log(String message) {
        if (Debuggle.instance != null) {
            Debuggle.instance.logger.info("[DEBUG] " + message);
        }
    }

    static void itsGoTime(VanishPlugin plugin) {
        Debuggle.instance = new Debuggle(plugin);
    }

    static void nah() {
        Debuggle.instance = null;
    }

    private Debuggle(VanishPlugin plugin) {
        this.logger = plugin.getLogger();
        this.logger.info("Debug enabled. Disable in config.yml");
    }
}