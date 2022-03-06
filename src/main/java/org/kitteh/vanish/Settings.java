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

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Matcher;

public final class Settings {
    private static boolean enablePermTest;
    private static String fakeQuit;
    private static String fakeJoin;
    private static boolean autoFakeJoinSilent;
    private static boolean worldChangeCheck;
    private static int lightningEffectCount;
    private static boolean doubleSneakDuringVanishSwitchesGameMode = false;
    private static int doubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS = 500;
    private static Component doubleSneakDuringVanishSwitchesGameModeMessageBack;
    private static Component doubleSneakDuringVanishSwitchesGameModeMessageSpec;

    private static final int confVersion = 10; // Tracking config version

    public static TextColor getDark() {
        return TextColor.fromHexString("#028090");//#05668D
    }

    public static TextColor getLight() {
        return TextColor.fromHexString("#7CFEF0");
    }

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

    public static ComponentLike getDoubleSneakDuringVanishSwitchesGameModeMessageBack() {
        return Settings.doubleSneakDuringVanishSwitchesGameModeMessageBack;
    }

    public static ComponentLike getDoubleSneakDuringVanishSwitchesGameModeMessageSpec() {
        return Settings.doubleSneakDuringVanishSwitchesGameModeMessageSpec;
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
                config.set("double-sneak-during-vanish-switches-gamemode.max-ms-time-between-sneaks", 750);
            }
            if (ver <= 9) {
                config.set("double-sneak-during-vanish-switches-gamemode.message", null);
                config.set("double-sneak-during-vanish-switches-gamemode.messagespec", "<#05668D>Game mode changed to spectator!");
                config.set("double-sneak-during-vanish-switches-gamemode.messageback", "<#05668D>Game mode restored!");
                config.set("fakeannounce.join", Settings.unlegacize(config.getString("fakeannounce.join", "%p joined the game.")));
                config.set("fakeannounce.quit", Settings.unlegacize(config.getString("fakeannounce.quit", "%p left the game.")));
            }
            config.set("configVersionDoNotTouch.SeriouslyThisWillEraseYourConfig", Settings.confVersion);
            plugin.saveConfig();
        }
        Settings.enablePermTest = config.getBoolean("permtest", false);
        Settings.fakeJoin = config.getString("fakeannounce.join", "<yellow><player:name> joined the game.");
        Settings.fakeQuit = config.getString("fakeannounce.quit", "<yellow><player:name> left the game.");
        Settings.autoFakeJoinSilent = config.getBoolean("fakeannounce.automaticforsilentjoin", false);
        Settings.worldChangeCheck = config.getBoolean("permissionsupdates.checkonworldchange", false);
        Settings.doubleSneakDuringVanishSwitchesGameMode = config.getBoolean("double-sneak-during-vanish-switches-gamemode.enabled", false);
        Settings.doubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS = config.getInt("double-sneak-during-vanish-switches-gamemode.max-ms-time-between-sneaks", 500);
        Settings.doubleSneakDuringVanishSwitchesGameModeMessageBack = MiniMessage.miniMessage().deserialize(config.getString("double-sneak-during-vanish-switches-gamemode.messageback", "<aqua>Game mode restored!"));
        Settings.doubleSneakDuringVanishSwitchesGameModeMessageSpec = MiniMessage.miniMessage().deserialize(config.getString("double-sneak-during-vanish-switches-gamemode.messagespec", "<aqua>Game mode changed to spectator!"));
        Settings.lightningEffectCount = config.getInt("effects.lightning.count", 30);
        if (Settings.lightningEffectCount < 1) {
            Settings.lightningEffectCount = 1;
        }
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Settings.fakeJoin = Settings.placeholderer(Settings.fakeJoin);
            Settings.fakeQuit = Settings.placeholderer(Settings.fakeQuit);
        }
        if (config.getBoolean("debug", false)) {
            Debuggle.itsGoTime(plugin);
        } else {
            Debuggle.nah();
        }
    }

    private static String unlegacize(String string) {
        string  = string.replace("&&", String.valueOf(org.bukkit.ChatColor.COLOR_CHAR));
        string = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacySection().deserialize(org.bukkit.ChatColor.YELLOW + string).compact());
        string = string.replace("%p", "<player:name>");
        string = string.replace("%d", "<player:displayname>");
        return string;
    }

    private static String placeholderer(String string) {
        Matcher matcher = PlaceholderAPI.getPlaceholderPattern().matcher(string);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            string = string.replace('%' + placeholder + '%', "<papi:" + placeholder + '>');
        }
        return string;
    }
}
