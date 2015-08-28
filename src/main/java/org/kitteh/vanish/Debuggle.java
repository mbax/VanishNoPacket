package org.kitteh.vanish;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

public final class Debuggle {
    private static final Logger LOGGER = Bukkit.getLogger();
    private static Debuggle instance = null;

    public static void log(String message) {
        if (Debuggle.instance != null) {
            Debuggle.LOGGER.log(Level.INFO, "[DEBUG] {0}", message);
        }
    }

    static void itsGoTime(VanishPlugin plugin) {
        Debuggle.instance = new Debuggle(plugin);
    }

    static void nah() {
        Debuggle.instance = null;
    }

    private Debuggle(VanishPlugin plugin) {
        Debuggle.LOGGER.info("Debug enabled. Disable in config.yml");
    }
}