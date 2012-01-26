package org.kitteh.vanish;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
    private static boolean enableColoration;
    private static boolean enablePermTest;
    private static String fakeQuit;
    private static String fakeJoin;
    private static boolean autoFakeJoinSilent;

    public static boolean autoFakeJoinSilent() {
        return Settings.autoFakeJoinSilent;
    }

    public static boolean enableColoration() {
        return Settings.enableColoration;
    }

    public static boolean enablePermTest() {
        return Settings.enablePermTest;
    }

    public static String fakeJoin() {
        return Settings.fakeJoin;
    }

    public static String fakeQuit() {
        return Settings.fakeQuit;
    }

    public static void freshStart(FileConfiguration config) {
        Settings.enableColoration = config.getBoolean("enableColoration", false);
        Settings.enablePermTest = config.getBoolean("permtest.enable", false);
        Settings.fakeJoin = config.getString("fakeannounce.join", "%p joined the game.").replace("&&", "§");
        Settings.fakeQuit = config.getString("fakeannounce.quit", "%p left the game.").replace("&&", "§");
        Settings.autoFakeJoinSilent = config.getBoolean("fakeannounce.automaticforsilentjoin", false);
        if (config.getBoolean("debug", false)) {
            Debuggle.itsGoTime();
        } else {
            Debuggle.nah();
        }
    }
}
