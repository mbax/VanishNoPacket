package org.kitteh.vanish;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

/**
 * It's the vanishing manager!
 * 
 * @author mbaxter
 * 
 */
public class VanishManager {

    private final VanishPlugin plugin;

    private ArrayList<Integer> listOfVanishedEntityIDs;
    private ArrayList<String> listOfVanishedPlayerNames;
    private HashMap<String, Boolean> sleepIgnored;

    private VanishAnnounceManipulator announceManipulator;

    public VanishManager(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return the Announce Manipulator
     */
    public VanishAnnounceManipulator getAnnounceManipulator() {
        return this.announceManipulator;
    }

    /**
     * @return Daddy!
     */
    public VanishPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Is the player vanished?
     * 
     * @param player
     * @return true if vanished
     */
    public boolean isVanished(Player player) {
        return this.listOfVanishedPlayerNames.contains(player.getName());
    }

    /**
     * @param playerName
     * @return if the named player is currently vanished
     */
    public boolean isVanished(String playerName) {
        final Player player = this.plugin.getServer().getPlayer(playerName);
        if (player != null) {
            Debuggle.log("Testing vanished status of " + player.getName() + ": " + this.isVanished(player));
            return this.isVanished(player);
        }
        Debuggle.log("Testing vanished status of " + playerName + ": null");
        return false;
    }

    /**
     * @return the number of players currently vanished
     */
    public int numVanished() {
        return this.listOfVanishedEntityIDs.size();
    }

    /**
     * Only call this when disabling the plugin
     */
    public void onPluginDisable() {
        this.revealAll();
    }

    public void playerQuit(Player player) {
        Debuggle.log("Quitting: " + player.getName());
        VanishPerms.userQuit(player);
        this.removeVanished(player.getName());
    }

    /**
     * Force a refresh of who a player can or can't see.
     * 
     * @param player
     */
    public void resetSeeing(Player player) {
        Debuggle.log("Resetting visibility on " + player.getName());
        if (VanishPerms.canSeeAll(player)) {
            this.showVanished(player);
        } else {
            this.hideVanished(player);
        }
    }


    /**
     * Smack that vanish list. Smack it hard.
     * But really, don't call this.
     */
    public void startup(boolean tabControl) {
        this.announceManipulator = new VanishAnnounceManipulator(this.plugin);
        this.listOfVanishedPlayerNames = new ArrayList<String>();
        this.sleepIgnored = new HashMap<String, Boolean>();
    }
    /**
     * Toggle a player's visibility
     * Called when a player calls /vanish
     * Talks to the player and everyone with vanish.see
     * 
     * @param togglingPlayer
     *            The player disappearing
     */
    public void toggleVanish(Player togglingPlayer) {
        this.toggleVanishQuiet(togglingPlayer);
        final String vanishingPlayerName = togglingPlayer.getName();
        String messageBit;
        final String base = ChatColor.YELLOW + vanishingPlayerName + " has ";
        if (this.isVanished(togglingPlayer)) {
            Debuggle.log("LoudVanishToggle Vanishing " + togglingPlayer.getName());
            this.plugin.hooksVanish(togglingPlayer);
            messageBit = "vanished. Poof.";

        } else {
            Debuggle.log("LoudVanishToggle Revealing " + togglingPlayer.getName());
            this.plugin.hooksUnvanish(togglingPlayer);
            messageBit = "become visible.";
            this.announceManipulator.vanishToggled(togglingPlayer);
        }
        final String message = base + messageBit;
        togglingPlayer.sendMessage(ChatColor.DARK_AQUA + "You have " + messageBit);
        this.plugin.messageStatusUpdate(message, togglingPlayer);
    }

    /**
     * Handle vanishing or unvanishing for a player
     * Does not say anything.
     * Called by toggleVanish(Player)
     * 
     * @param vanishingPlayer
     */
    public void toggleVanishQuiet(Player vanishingPlayer) {
        final boolean vanishing = !this.isVanished(vanishingPlayer);
        final String vanishingPlayerName = vanishingPlayer.getName();
        if (vanishing) {
            Debuggle.log("It's invisible time! " + vanishingPlayer.getName());
            this.sleepIgnored.put(vanishingPlayerName, vanishingPlayer.isSleepingIgnored());
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", true);
            this.addVanished(vanishingPlayerName);
            this.plugin.log(vanishingPlayerName + " disappeared.");
        } else {
            Debuggle.log("It's visible time! " + vanishingPlayer.getName());
            vanishingPlayer.setSleepingIgnored(this.sleepIgnored.remove(vanishingPlayerName));
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", false);
            this.removeVanished(vanishingPlayerName);
            this.plugin.log(vanishingPlayerName + " reappeared.");
        }
        final Player[] playerList = this.plugin.getServer().getOnlinePlayers();
        for (final Player otherPlayer : playerList) {
            if (vanishingPlayer.equals(otherPlayer)) {
                continue;
            }
            Debuggle.log("Determining what to do about " + vanishingPlayer.getName() + " for " + otherPlayer.getName());
            if (vanishing && !VanishPerms.canSeeAll(otherPlayer)) {
                    this.destroyEntity(vanishingPlayer, otherPlayer);
            } else 
                if (!VanishPerms.canSeeAll(otherPlayer)) {
                    this.undestroyEntity(vanishingPlayer, otherPlayer);
                }
            
        }
    }

    private void addVanished(String name) {
        this.listOfVanishedPlayerNames.add(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void destroyEntity(Player vanishingPlayer, Player obliviousPlayer) {
        Debuggle.log("Hiding " + vanishingPlayer.getName() + " from " + obliviousPlayer.getName());
        obliviousPlayer.hidePlayer(vanishingPlayer);
    }

    private void hideVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.isVanished(otherPlayer)) {
                this.destroyEntity(otherPlayer, player);
            }
        }
    }

    private void removeVanished(String name) {
        this.listOfVanishedPlayerNames.remove(name);
    }

    private void revealAll() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            for (final Player player2 : this.plugin.getServer().getOnlinePlayers()) {
                if ((player != null) && (player2 != null) && !player.equals(player2)) {
                    if(!player.canSee(player2)){
                        player.showPlayer(player2);
                    }
                }
            }
        }
    }

    private void showVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.isVanished(otherPlayer)) {
                this.undestroyEntity(otherPlayer, player);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void undestroyEntity(Player revealPlayer, Player nowAwarePlayer) {
        Debuggle.log("Revealing " + revealPlayer.getName() + " from " + nowAwarePlayer.getName());
        nowAwarePlayer.showPlayer(revealPlayer);
    }

}