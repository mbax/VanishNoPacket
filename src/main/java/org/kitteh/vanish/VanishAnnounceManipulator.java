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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.hooks.plugins.VaultHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller of announcing joins and quits that aren't their most honest.
 * Note that delayed announce methods can be called without checking
 * to see if it's enabled first. The methods confirm before doing anything
 * particularly stupid.
 */
public final class VanishAnnounceManipulator {
    private final List<String> delayedAnnouncePlayerList;
    private final VanishPlugin plugin;
    private final Map<String, Boolean> playerOnlineStatus;
    private final boolean placeholderAPI;

    VanishAnnounceManipulator(@NonNull VanishPlugin plugin) {
        this.plugin = plugin;
        this.playerOnlineStatus = new HashMap<>();
        this.delayedAnnouncePlayerList = new ArrayList<>();
        this.placeholderAPI = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public void addToDelayedAnnounce(@NonNull String player) {
        this.playerOnlineStatus.put(player, false);
        if (Settings.getAutoFakeJoinSilent()) {
            this.delayedAnnouncePlayerList.add(player);
        }
    }

    /**
     * Removes a player's delayed announce
     *
     * @param player name of the player
     */
    public void dropDelayedAnnounce(@NonNull String player) {
        this.delayedAnnouncePlayerList.remove(player);
    }

    /**
     * Gets the fake online status of a player
     *
     * @param playerName name of the player to query
     * @return true if player is considered online, false if not (or if not on server)
     */
    public boolean getFakeOnlineStatus(@NonNull String playerName) {
        final Player player = this.plugin.getServer().getPlayerExact(playerName);
        if (player == null) {
            return false;
        }
        playerName = player.getName();
        return this.playerOnlineStatus.getOrDefault(playerName, true);
    }

    /**
     * Marks a player as quit
     * Called when a player quits
     *
     * @param player name of the player who just quit
     * @return the former fake online status of the player
     */
    public boolean playerHasQuit(@NonNull String player) {
        if (this.playerOnlineStatus.containsKey(player)) {
            return this.playerOnlineStatus.remove(player);
        }
        return true;
    }

    private @NonNull String injectPlayerInformation(@NonNull String message, @NonNull Player player) {
        final VaultHook vault = (VaultHook) this.plugin.getHookManager().getHook(HookType.Vault);
        message = message.replace("%p", player.getName());
        message = message.replace("%d", player.getDisplayName());
        if (this.placeholderAPI) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    void fakeJoin(@NonNull Player player, boolean force) {
        if (force || !(this.playerOnlineStatus.containsKey(player.getName()) && this.playerOnlineStatus.get(player.getName()))) {
            this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeJoin(), player));
            this.plugin.getLogger().info(player.getName() + " faked joining");
            this.playerOnlineStatus.put(player.getName(), true);
            this.plugin.hooksFakeJoin(player);
        }
    }

    void fakeQuit(@NonNull Player player, boolean force) {
        if (force || !(this.playerOnlineStatus.containsKey(player.getName()) && !this.playerOnlineStatus.get(player.getName()))) {
            this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeQuit(), player));
            this.plugin.getLogger().info(player.getName() + " faked quitting");
            this.playerOnlineStatus.put(player.getName(), false);
            this.plugin.hooksFakeQuit(player);
        }
    }

    void vanishToggled(@NonNull Player player) {
        if (Settings.getAutoFakeJoinSilent() && this.delayedAnnouncePlayerList.contains(player.getName())) {
            this.fakeJoin(player, false);
            this.dropDelayedAnnounce(player.getName());
        }
    }
}
