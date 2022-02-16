/*
 * VanishNoPacket
 * Copyright (C) 2011-2022 Matt Baxter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.kitteh.vanish;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class Settings {
    private static boolean enablePermTest;
    private static String fakeQuit;
    private static String fakeJoin;
    private static boolean autoFakeJoinSilent;
    private static boolean worldChangeCheck;
    private static int lightningEffectCount;
    private static boolean doubleSneakDuringVanishSwitchesGameMode = false;
    private static int doubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS = 500;
    private static String doubleSneakDuringVanishSwitchesGameModeMessage = "&aGameMode changed!";

    private static final int confVersion = 9; // Tracking config version

    public static boolean getAutoFakeJoinSilent() {
        return Settings.autoFakeJoinSilent;
    }

    public static boolean getEnablePermTest() {
        return Settings.enablePermTest;
    }

    public static String getFakeJoin() {
        return Settings.fakeJoin;
    }

    public static String getFakeQuit() {
        return Settings.fakeQuit;
    }

    public static int getLightningCount() {
        return Settings.lightningEffectCount;
    }

    public static boolean getWorldChangeCheck() {
        return Settings.worldChangeCheck;
    }

    public static boolean isDoubleSneakDuringVanishSwitchesGameMode() {
        return Settings.doubleSneakDuringVanishSwitchesGameMode;
    }

    public static int getDoubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS() {
        return Settings.doubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS;
    }

    public static String getDoubleSneakDuringVanishSwitchesGameModeMessage() {
        return Settings.doubleSneakDuringVanishSwitchesGameModeMessage;
    }

    static void freshStart(@NonNull VanishPlugin plugin) {
        final FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        final int ver = config.getInt("configVersionDoNotTouch.SeriouslyThisWillEraseYourConfig", 0);
        if (ver != Settings.confVersion) {
            plugin.getLogger().info("Attempting to update your configuration. Check to make sure it's ok");
            if (ver < 1) {
                config.set("spoutcraft.enable", null);
                config.set("spoutcraft", null);
            }
            if ((ver <= 1) || config.contains("permtest.enable")) {
                final boolean permtest = config.getBoolean("permtest.enable", false);
                config.set("permtest.enable", null);
                config.set("permtest", permtest);
                config.set("enableColoration", null);
                config.set("enableTabControl", null);
                config.set("updates.check", null);
            }
            if ((ver <= 3)) {
                config.set("effects.lightning.count", 30);
            }
            if (ver <= 5) {
                config.set("hooks.JSONAPI", null);
                config.set("hooks.spoutcraft", null);
                config.set("colornametags", null);
                config.set("checkupdates", null);
            }
            if (ver <= 6) {
                config.set("hooks.dynmap", false);
            }
            if (ver <= 7) {
                config.set("hooks.discordsrv", false);
            }
            if (ver <= 8) {
                config.set("hooks.squaremap", false);
                config.set("double-sneak-during-vanish-switches-gamemode.enabled", false);
                config.set("double-sneak-during-vanish-switches-gamemode.max-ms-time-between-sneaks", 500);
                config.set("double-sneak-during-vanish-switches-gamemode.message", "&aGameMode changed!");
            }
            config.set("configVersionDoNotTouch.SeriouslyThisWillEraseYourConfig", Settings.confVersion);
            plugin.saveConfig();
        }
        Settings.enablePermTest = config.getBoolean("permtest", false);
        Settings.fakeJoin = config.getString("fakeannounce.join", "%p joined the game.").replace("&&", String.valueOf(ChatColor.COLOR_CHAR));
        Settings.fakeQuit = config.getString("fakeannounce.quit", "%p left the game.").replace("&&", String.valueOf(ChatColor.COLOR_CHAR));
        Settings.autoFakeJoinSilent = config.getBoolean("fakeannounce.automaticforsilentjoin", false);
        Settings.worldChangeCheck = config.getBoolean("permissionsupdates.checkonworldchange", false);
        Settings.doubleSneakDuringVanishSwitchesGameMode = config.getBoolean("double-sneak-during-vanish-switches-gamemode.enabled", false);
        Settings.doubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS = config.getInt("double-sneak-during-vanish-switches-gamemode.max-ms-time-between-sneaks", 500);
        Settings.doubleSneakDuringVanishSwitchesGameModeMessage = config.getString("double-sneak-during-vanish-switches-gamemode.message", "&aGameMode changed!");
        Settings.lightningEffectCount = config.getInt("effects.lightning.count", 30);
        if (Settings.lightningEffectCount < 1) {
            Settings.lightningEffectCount = 1;
        }
        if (config.getBoolean("debug", false)) {
            Debuggle.itsGoTime(plugin);
        } else {
            Debuggle.nah();
        }
    }
}
