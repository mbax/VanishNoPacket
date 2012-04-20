package org.kitteh.vanish;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class Debuggle {
    private final Logger logger;
    private static Debuggle instance = null;

    public static void itsGoTime() {
        Bukkit.getLogger().info("[VanishNoPacket] " + Messages.getString("Debuggle.DebugEnabled"));
        Debuggle.instance = new Debuggle();
    }

    public static void log(String message) {
        if (Debuggle.instance != null) {
            Debuggle.instance.logger.info("[VNP-DEBUG] " + message);
        }
    }

    public static void nah() {
        Debuggle.instance = null;
    }

    public Debuggle() {
        this.logger = Logger.getLogger("Minecraft");
    }
}
