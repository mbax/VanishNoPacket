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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kitteh.vanish.hooks.HookManager;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.listeners.ListenEntity;
import org.kitteh.vanish.listeners.ListenInventory;
import org.kitteh.vanish.listeners.ListenPaper;
import org.kitteh.vanish.listeners.ListenPlayerJoin;
import org.kitteh.vanish.listeners.ListenPlayerMessages;
import org.kitteh.vanish.listeners.ListenPlayerOther;
import org.kitteh.vanish.listeners.ListenServerPing;
import org.kitteh.vanish.listeners.ListenToYourHeart;

import java.io.File;
import java.util.HashSet;

public final class VanishPlugin extends JavaPlugin implements Listener {
    private final HashSet<String> haveInventoriesOpen = new HashSet<>();
    private final HookManager hookManager = new HookManager(this);
    private VanishManager manager;

    /**
     * Informs VNP that a user has closed their fake chest
     *
     * @param name user's name
     */
    public void chestFakeClose(@NonNull String name) {
        synchronized (this.haveInventoriesOpen) {
            this.haveInventoriesOpen.remove(name);
        }
    }

    /**
     * Queries if a user is currently using a fake chest
     *
     * @param name the user's name
     * @return true if currently using a fake chest
     */
    public boolean chestFakeInUse(@NonNull String name) {
        synchronized (this.haveInventoriesOpen) {
            return this.haveInventoriesOpen.contains(name);
        }
    }

    /**
     * Informs VNP that a user has opened their fake chest
     *
     * @param name user's name
     */
    public void chestFakeOpen(@NonNull String name) {
        synchronized (this.haveInventoriesOpen) {
            this.haveInventoriesOpen.add(name);
        }
    }

    /**
     * Gets the current version
     *
     * @return version of VanishNoPacket in use
     */
    public @NonNull String getCurrentVersion() {
        return this.getDescription().getVersion();
    }

    /**
     * Gets the hook manager
     *
     * @return the hook manager
     */
    public @NonNull HookManager getHookManager() {
        return this.hookManager;
    }

    /**
     * Gets the vanish manager
     *
     * @return the VanishManager
     */
    public @NonNull VanishManager getManager() {
        return this.manager;
    }

    /**
     * Indicates a player has just joined the server.
     * Internal use only please
     *
     * @param player player who has joined the server
     */
    public void hooksJoin(@NonNull Player player) {
        this.hookManager.onJoin(player);
    }

    /**
     * Indicates a player has left the server
     * Internal use only please
     *
     * @param player player who has left the server
     */
    public void hooksQuit(@NonNull Player player) {
        this.hookManager.onQuit(player);
        this.hookManager.onUnvanish(player);
    }

    /**
     * Calls hooks for when a player has unvanished
     * Internal use only please
     *
     * @param player the un-vanishing user
     */
    public void hooksUnvanish(@NonNull Player player) {
        this.hookManager.onUnvanish(player);
    }

    /**
     * Calls hooks for when player has vanished
     * Internal use only please.
     *
     * @param player the vanishing player
     */
    public void hooksVanish(@NonNull Player player) {
        this.hookManager.onVanish(player);
    }

    /**
     * Calls hooks for when player has sent a fake join message
     * Internal use only please.
     *
     * @param player the fake joining player
     */
    public void hooksFakeJoin(@NonNull Player player) { this.hookManager.onFakeJoin(player); }

    /**
     * Calls hooks for when player has sent a fake quit message
     * Internal use only please.
     *
     * @param player the fake quitting player
     */
    public void hooksFakeQuit(@NonNull Player player) { this.hookManager.onFakeQuit(player); }

    /**
     * Sends a message to all players with vanish.statusupdates permission
     *
     * @param message the message to send
     */
    public void messageStatusUpdate(@NonNull String message) {
        this.messageStatusUpdate(message, null);
    }

    /**
     * Sends a message to all players with vanish.statusupdates but one
     *
     * @param message the message to send
     * @param avoid   player to not send the message to
     */
    public void messageStatusUpdate(@NonNull String message, @Nullable Player avoid) {
        for (final Player player : this.getServer().getOnlinePlayers()) {
            if ((player != null) && !player.equals(avoid) && VanishPerms.canSeeStatusUpdates(player)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void onDisable() {
        this.setInstance(null);
        Debuggle.nah();
        for (final Player player : VanishPlugin.this.getServer().getOnlinePlayers()) {
            if (player != null) {
                if (this.manager.isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "[Vanish] You have been forced visible by a reload.");
                }
            }
        }
        this.hookManager.onDisable();
        this.manager.onPluginDisable();
        this.getLogger().info(this.getCurrentVersion() + " unloaded.");
    }

    @Override
    public void onEnable() {
        this.setInstance(this);

        // Thanks, PaperLib
        // https://github.com/PaperMC/PaperLib
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            this.getServer().getPluginManager().registerEvents(new ListenPaper(this), this);
        } catch (ClassNotFoundException ignored) {
            final String benefitsProperty = "paperlib.shown-benefits";
            this.getLogger().warning("====================================================");
            this.getLogger().warning(" VanishNoPacket works better if you use Paper ");
            this.getLogger().warning(" as your server software. ");
            if (System.getProperty(benefitsProperty) == null) {
                System.setProperty(benefitsProperty, "1");
                this.getLogger().warning("  ");
                this.getLogger().warning(" Paper offers significant performance improvements,");
                this.getLogger().warning(" bug fixes, security enhancements and optional");
                this.getLogger().warning(" features for server owners to enhance their server.");
                this.getLogger().warning("  ");
                this.getLogger().warning(" Paper includes Timings v2, which is significantly");
                this.getLogger().warning(" better at diagnosing lag problems over v1.");
                this.getLogger().warning("  ");
                this.getLogger().warning(" All of your plugins should still work, and the");
                this.getLogger().warning(" Paper community will gladly help you fix any issues.");
                this.getLogger().warning("  ");
                this.getLogger().warning(" Join the Paper Community @ https://papermc.io");
            }
            this.getLogger().warning("====================================================");
        }

        final File check = new File(this.getDataFolder(), "config.yml");
        boolean firstTimeStarting = false;
        if (!check.exists()) {
            this.saveDefaultConfig();
            this.reloadConfig();
            if (this.getServer().getPluginManager().isPluginEnabled("Essentials")) {
                this.getConfig().set("hooks.essentials", true);
                this.getLogger().info("Detected Essentials. Enabling Essentials hook.");
                this.saveConfig();
            }
        }

        Settings.freshStart(this);

        if (this.getConfig().getBoolean("hooks.essentials", false)) {
            this.hookManager.getHook(HookType.Essentials).onEnable();
        }
        if (this.getConfig().getBoolean("hooks.dynmap", false)) {
            this.hookManager.getHook(HookType.Dynmap).onEnable();
        }
        if (this.getConfig().getBoolean("hooks.discordsrv", false) && this.getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            // Shouldn't happen here, but if the load order gets broken...
            this.hookManager.getHook(HookType.DiscordSRV).onEnable();
        }
        if (this.getConfig().getBoolean("hooks.squaremap", false)) {
            this.hookManager.getHook(HookType.squaremap).onEnable();
        }

        final VanishPlugin self = this;

        this.manager = new VanishManager(this);

        for (final Player player : this.getServer().getOnlinePlayers()) {
            player.setMetadata("vanished", new LazyMetadataValue(this, CacheStrategy.NEVER_CACHE, new VanishCheck(this.manager, player.getName())));
        }

        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new ListenEntity(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerMessages(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerOther(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenToYourHeart(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenInventory(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenServerPing(this.manager), this);

        this.getLogger().info(this.getCurrentVersion() + " loaded.");
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("DiscordSRV") && this.getConfig().getBoolean("hooks.discordsrv", false)) {
            this.hookManager.getHook(HookType.DiscordSRV).onEnable();
        }
    }

    /**
     * Reloads the VNP config
     */
    public void reload() {
        this.reloadConfig();
        Settings.freshStart(this);
    }

    @SuppressWarnings("deprecation")
    private void setInstance(@Nullable VanishPlugin plugin) {
        org.kitteh.vanish.staticaccess.VanishNoPacket.setInstance(plugin);
    }
}
