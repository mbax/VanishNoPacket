package org.kitteh.vanish;

import java.util.logging.Logger;

public class Debuggle {
    private final Logger logger;
    private static Debuggle instance = null;

    public static void itsGoTime() {
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
