package org.kitteh.vanish;

import java.util.*;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.metrics.MetricsOverlord;

/**
 * It's the vanishing manager!
 * 
 * @author mbaxter
 * 
 */
public class VanishManager {

    private final VanishPlugin plugin;

    private Set<String> vanishedPlayerNames;
    private Map<String, Boolean> sleepIgnored;

    private VanishAnnounceManipulator announceManipulator;

    private final Random random = new Random();

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
        return this.vanishedPlayerNames.contains(player.getName());
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
        return this.vanishedPlayerNames.size();
    }

    /**
     * Only call this when disabling the plugin
     */
    public void onPluginDisable() {
        this.revealAll();
    }

    public void playerQuit(Player player) {
        Debuggle.log("Quitting: " + player.getName());
        this.resetSleepingIgnored(player);
        VanishPerms.userQuit(player);
        this.removeVanished(player.getName());
    }

    /**
     * Reset the player's info
     * 
     * @param player
     */
    public void playerRefresh(Player player) {
        this.resetSeeing(player);
        if (this.isVanished(player) && !VanishPerms.canVanish(player)) {
            this.toggleVanish(player);
        }
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
            Debuggle.log("Showing all to " + player.getName());
        } else {
            this.hideVanished(player);
            Debuggle.log("Hiding all to " + player.getName());
        }
    }

    /**
     * Reset SleepingIgnored to its old value for a player.
     * 
     * @param player
     */
    public void resetSleepingIgnored(Player player) {
        if (this.sleepIgnored.containsKey(player.getName())) {
            player.setSleepingIgnored(this.sleepIgnored.remove(player.getName()));
        }
    }

    /**
     * Set SleepingIgnored to true for a player, and save the old
     * value.
     * 
     * @param player
     */
    public void setSleepingIgnored(Player player) {
        if (!this.sleepIgnored.containsKey(player.getName())) {
            this.sleepIgnored.put(player.getName(), player.isSleepingIgnored());
        }
        player.setSleepingIgnored(true);
    }

    /**
     * Smack that vanish list. Smack it hard.
     * But really, don't call this.
     */
    public void startup() {
        this.announceManipulator = new VanishAnnounceManipulator(this.plugin);
        this.vanishedPlayerNames = new HashSet<String>();
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
        final CraftPlayer cplr = ((CraftPlayer) vanishingPlayer);
        if (vanishing) {
            Debuggle.log("It's invisible time! " + vanishingPlayer.getName());
            this.setSleepingIgnored(vanishingPlayer);
            if (VanishPerms.canNotFollow(vanishingPlayer)) {
                for (final Entity entity : vanishingPlayer.getNearbyEntities(100, 100, 100)) {
                    if (entity != null && entity instanceof Creature) {
                        final Creature creature = ((Creature) entity);
                        if (creature != null && creature.getTarget()!=null && creature.getTarget().equals(vanishingPlayer)) {
                            creature.setTarget(null);
                        }
                    }
                }
            }
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", true);
            this.addVanished(vanishingPlayerName);
            if (VanishPerms.canSmoke(vanishingPlayer)) {
                this.smokeScreenEffect(vanishingPlayer.getLocation());
            }
            cplr.getHandle().netServerHandler.sendPacket(new Packet41MobEffect(cplr.getEntityId(), new MobEffect(MobEffectList.INVISIBILITY.getId(), 0, 0)));
            MetricsOverlord.vanish.increment();
            this.plugin.log(vanishingPlayerName + " disappeared.");
        } else {
            Debuggle.log("It's visible time! " + vanishingPlayer.getName());
            this.resetSleepingIgnored(vanishingPlayer);
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", false);
            this.removeVanished(vanishingPlayerName);

            if (VanishPerms.canSmoke(vanishingPlayer)) {
                this.smokeScreenEffect(vanishingPlayer.getLocation());
            }
            cplr.getHandle().netServerHandler.sendPacket(new Packet42RemoveMobEffect(cplr.getEntityId(), new MobEffect(MobEffectList.INVISIBILITY.getId(), 0, 0)));
            MetricsOverlord.unvanish.increment();
            this.plugin.log(vanishingPlayerName + " reappeared.");
        }
        this.plugin.getServer().getPluginManager().callEvent(new VanishStatusChangeEvent(vanishingPlayerName, vanishing));
        final Player[] playerList = this.plugin.getServer().getOnlinePlayers();
        for (final Player otherPlayer : playerList) {
            if (vanishingPlayer.equals(otherPlayer)) {
                continue;
            }
            Debuggle.log("Determining what to do about " + vanishingPlayer.getName() + " for " + otherPlayer.getName());
            if (vanishing && !VanishPerms.canSeeAll(otherPlayer) && otherPlayer.canSee(vanishingPlayer)) {
                Debuggle.log("Hiding " + vanishingPlayer.getName() + " from " + otherPlayer.getName());
                otherPlayer.hidePlayer(vanishingPlayer);
            } else if ((!vanishing || VanishPerms.canSeeAll(otherPlayer)) && !otherPlayer.canSee(vanishingPlayer)) {
                Debuggle.log("Showing " + vanishingPlayer.getName() + " to " + otherPlayer.getName());
                otherPlayer.showPlayer(vanishingPlayer);
            }

        }
    }

    private void addVanished(String name) {
        this.vanishedPlayerNames.add(name);
    }

    private void hideVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.isVanished(otherPlayer) && player.canSee(otherPlayer)) {
                player.hidePlayer(otherPlayer);
            }
        }
    }

    private void removeVanished(String name) {
        this.vanishedPlayerNames.remove(name);
    }

    private void revealAll() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            for (final Player player2 : this.plugin.getServer().getOnlinePlayers()) {
                if ((player != null) && (player2 != null) && !player.equals(player2)) {
                    if (!player.canSee(player2)) {
                        player.showPlayer(player2);
                    }
                }
            }
        }
    }

    private void showVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.isVanished(otherPlayer) && !player.canSee(otherPlayer)) {
                player.showPlayer(otherPlayer);
            }
        }
    }

    private void smokeScreenEffect(Location location) {
        for (int i = 0; i < 10; i++) {
            location.getWorld().playEffect(location, Effect.SMOKE, this.random.nextInt(9));
        }
    }

}