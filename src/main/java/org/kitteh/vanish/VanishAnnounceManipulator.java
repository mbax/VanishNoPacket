package org.kitteh.vanish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.vanish.event.VanishFakeJoinEvent;
import org.kitteh.vanish.event.VanishFakeQuitEvent;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.hooks.plugins.BPermissionsHook;
import org.kitteh.vanish.hooks.plugins.GeoIPToolsHook;
import org.kitteh.vanish.hooks.plugins.VaultHook;
import org.kitteh.vanish.metrics.MetricsOverlord;

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

    VanishAnnounceManipulator(VanishPlugin plugin) {
        this.plugin = plugin;
        this.playerOnlineStatus = new HashMap<String, Boolean>();
        this.delayedAnnouncePlayerList = new ArrayList<String>();
    }

    public void addToDelayedAnnounce(String player) {
        this.playerOnlineStatus.put(player, false);
        if (!Settings.getAutoFakeJoinSilent()) {
            return;
        }
        this.delayedAnnouncePlayerList.add(player);
    }

    /**
     * Removes a player's delayed announce
     *
     * @param player name of the player
     */
    public void dropDelayedAnnounce(String player) {
        this.delayedAnnouncePlayerList.remove(player);
    }

    /**
     * Gets the fake online status of a player
     * Called by JSONAPI
     *
     * @param playerName name of the player to query
     * @return true if player is considered online, false if not (or if not on server)
     */
    public boolean getFakeOnlineStatus(String playerName) {
        final Player player = this.plugin.getServer().getPlayerExact(playerName);
        if (player == null) {
            return false;
        }
        playerName = player.getName();
        if (this.playerOnlineStatus.containsKey(playerName)) {
            return this.playerOnlineStatus.get(playerName);
        } else {
            return true;
        }
    }

    /**
     * Marks a player as quit
     * Called when a player quits
     * 
     * @param player name of the player who just quit
     * @return the former fake online status of the player
     */
    public boolean playerHasQuit(String player) {
        if (this.playerOnlineStatus.containsKey(player)) {
            return this.playerOnlineStatus.remove(player);
        }
        return true;
    }

    private String injectPlayerInformation(String message, Player player) {
        final GeoIPToolsHook geoip = (GeoIPToolsHook) this.plugin.getHookManager().getHook(HookType.GeoIPTools);
        final BPermissionsHook bperms = (BPermissionsHook) this.plugin.getHookManager().getHook(HookType.BPermissions);
        final VaultHook vault = (VaultHook) this.plugin.getHookManager().getHook(HookType.Vault);
        message = message.replace("%p", player.getName());
        message = message.replace("%d", player.getDisplayName());
        String prefix = bperms.getPrefix(player);
        if (prefix.isEmpty()) {
            prefix = vault.getPrefix(player);
        }
        message = message.replace("%up", prefix);
        String suffix = bperms.getSuffix(player);
        if (suffix.isEmpty()) {
            suffix = vault.getSuffix(player);
        }
        message = message.replace("%us", suffix);
        message = message.replace("%city", geoip.getCity(player));
        message = message.replace("%country", geoip.getCountry(player));
        return message;
    }

    void fakeJoin(Player player, boolean force) {
        if (force || !(this.playerOnlineStatus.containsKey(player.getName()) && this.playerOnlineStatus.get(player.getName()))) {
            this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeJoin(), player));
            this.plugin.getLogger().info(player.getName() + " faked joining");
            this.plugin.getServer().getPluginManager().callEvent(new VanishFakeJoinEvent(player, "has joined."));
            MetricsOverlord.getFakejoinTracker().increment();
            this.playerOnlineStatus.put(player.getName(), true);
        }
    }

    void fakeQuit(Player player, boolean force) {
        if (force || !(this.playerOnlineStatus.containsKey(player.getName()) && !this.playerOnlineStatus.get(player.getName()))) {
            this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeQuit(), player));
            this.plugin.getLogger().info(player.getName() + " faked quitting");
            this.plugin.getServer().getPluginManager().callEvent(new VanishFakeQuitEvent(player, "has quit."));
            MetricsOverlord.getFakequitTracker().increment();
            this.playerOnlineStatus.put(player.getName(), false);
        }
    }

    void vanishToggled(Player player) {
        if (!Settings.getAutoFakeJoinSilent() || !this.delayedAnnouncePlayerList.contains(player.getName())) {
            return;
        }
        this.fakeJoin(player, false);
        this.dropDelayedAnnounce(player.getName());
    }
}