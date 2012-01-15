package org.kitteh.vanish;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Controller of announcing joins and quits that aren't their most honest.
 * Note that delayed announce methods can be called without checking
 * to see if it's enabled first. The methods confirm before doing anything
 * particularly stupid.
 */
public class VanishAnnounceManipulator {
    private ArrayList<String> delayedAnnouncePlayerList;
    private final Object syncDelayedAnnouncePlayerList = new Object();
    private final VanishPlugin plugin;
    private final String fakeJoinMessage;
    private final String fakeQuitMessage;
    private final boolean delayedJoinTrackingEnabled;
    private final HashMap<String, Boolean> playerOnlineStatus;

    /**
     * @param plugin
     *            The running instance of the plugin
     * @param fakeJoinMessage
     *            The message to display. && for color, %p for player.getName(),
     *            %d for player.getDisplayName()
     * @param fakeQuitMessage
     * @param delayedJoinTrackingEnabled
     */
    public VanishAnnounceManipulator(VanishPlugin plugin, String fakeJoinMessage, String fakeQuitMessage, boolean delayedJoinTrackingEnabled) {
        this.plugin = plugin;
        this.fakeJoinMessage = fakeJoinMessage.replace("&&", "§");
        this.fakeQuitMessage = fakeQuitMessage.replace("&&", "§");
        this.delayedJoinTrackingEnabled = delayedJoinTrackingEnabled;
        this.playerOnlineStatus = new HashMap<String, Boolean>();
        synchronized (this.syncDelayedAnnouncePlayerList) {
            this.delayedAnnouncePlayerList = new ArrayList<String>();
        }
    }

    public void addToDelayedAnnounce(String player) {
        this.playerOnlineStatus.put(player, false);
        if (!this.delayedJoinTrackingEnabled) {
            return;
        }
        synchronized (this.syncDelayedAnnouncePlayerList) {
            this.delayedAnnouncePlayerList.add(player);
        }
    }

    /**
     * Remove a player's delayed announce
     * 
     * @param player
     */
    public void dropDelayedAnnounce(String player) {
        synchronized (this.syncDelayedAnnouncePlayerList) {
            this.delayedAnnouncePlayerList.remove(player);
        }
    }

    /**
     * Call a fake join announce for the player.
     * Only fires if the server previously was saying they were offline
     * 
     * @param player
     */
    public void fakeJoin(Player player) {
        if (this.playerOnlineStatus.containsKey(player) && this.playerOnlineStatus.get(player)) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(this.fakeJoinMessage, player));
        this.plugin.log(player.getName() + " faked joining");
        this.playerOnlineStatus.put(player.getName(), true);
    }

    /**
     * Call a fake quit for the player.
     * Only fires if the server previously was saying they were online
     * 
     * @param player
     */
    public void fakeQuit(Player player) {
        if (this.playerOnlineStatus.containsKey(player) && !this.playerOnlineStatus.get(player.getName())) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(this.fakeQuitMessage, player));
        this.plugin.log(player.getName() + " faked quitting");
        this.playerOnlineStatus.put(player.getName(), false);
    }

    /**
     * Called when a player's vanish status has been toggled
     * If the player has a queued up join announce from a silentjoin,
     * it will fire at this time.
     * 
     * @param player
     */
    public void vanishToggled(Player player) {
        if (!this.delayedJoinTrackingEnabled || !this.delayedAnnouncePlayerList.contains(player)) {
            return;
        }
        this.fakeJoin(player);
        this.dropDelayedAnnounce(player.getName());
    }

    /**
     * Called when a player quits
     * 
     * @param player
     *            Who just quit?
     * @return the former fake online status of the player
     */
    public boolean wasPlayerMarkedOnline(String player) {
        if (this.playerOnlineStatus.containsKey(player)) {
            return this.playerOnlineStatus.remove(player);
        }
        return true;
    }

    private String injectPlayerInformation(String message, Player player) {
        return message.replace("%p", player.getName()).replace("%d", player.getDisplayName()).replace("%up", this.plugin.getBPerms().getPrefix(player)).replace("%us", this.plugin.getBPerms().getSuffix(player));
    }
}
