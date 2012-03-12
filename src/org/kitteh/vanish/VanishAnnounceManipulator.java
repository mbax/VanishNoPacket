package org.kitteh.vanish;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.vanish.metrics.MetricsOverlord;

/**
 * Controller of announcing joins and quits that aren't their most honest.
 * Note that delayed announce methods can be called without checking
 * to see if it's enabled first. The methods confirm before doing anything
 * particularly stupid.
 */
public class VanishAnnounceManipulator {
    private final ArrayList<String> delayedAnnouncePlayerList;
    private final VanishPlugin plugin;
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
    public VanishAnnounceManipulator(VanishPlugin plugin) {
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
     * Remove a player's delayed announce
     * 
     * @param player
     */
    public void dropDelayedAnnounce(String player) {
        this.delayedAnnouncePlayerList.remove(player);
    }

    /**
     * Call a fake join announce for the player.
     * Only fires if the server previously was saying they were offline
     * 
     * @param player
     */
    public void fakeJoin(Player player) {
        if (this.playerOnlineStatus.containsKey(player.getName()) && this.playerOnlineStatus.get(player.getName())) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeJoin(), player));
        this.plugin.log(player.getName() + " faked joining");
        MetricsOverlord.fakejoin.increment();
        this.playerOnlineStatus.put(player.getName(), true);
    }
    
    /**
     * Call a forced fake quit for the player.
     * Fires regardless of wether the server previously said they were online
     * 
     * @param player
     */
    public void forceFakeQuit(Player player){
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeQuit(), player));
        this.plugin.log(player.getName() + " faked quitting");
        MetricsOverlord.fakequit.increment();
        this.playerOnlineStatus.put(player.getName(), false);
    }

    /**
     * Call a fake quit for the player.
     * Only fires if the server previously was saying they were online
     * 
     * @param player
     */
    public void fakeQuit(Player player) {
        if (this.playerOnlineStatus.containsKey(player.getName()) && !this.playerOnlineStatus.get(player.getName())) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + this.injectPlayerInformation(Settings.getFakeQuit(), player));
        this.plugin.log(player.getName() + " faked quitting");
        MetricsOverlord.fakequit.increment();
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
        if (!Settings.getAutoFakeJoinSilent() || !this.delayedAnnouncePlayerList.contains(player.getName())) {
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
