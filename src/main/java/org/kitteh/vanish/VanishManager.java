package org.kitteh.vanish;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.metrics.MetricsOverlord;

import com.google.common.collect.ImmutableSet;

public final class VanishManager {
    private final class ShowPlayerEntry {
        private final Player player;
        private final Player target;

        public ShowPlayerEntry(Player player, Player target) {
            this.player = player;
            this.target = target;
        }

        public Player getPlayer() {
            return this.player;
        }

        public Player getTarget() {
            return this.target;
        }
    }

    private final class ShowPlayerHandler implements Runnable {
        Set<ShowPlayerEntry> entries = new HashSet<ShowPlayerEntry>();
        Set<ShowPlayerEntry> next = new HashSet<ShowPlayerEntry>();

        public void add(ShowPlayerEntry player) {
            this.entries.add(player);
        }

        @Override
        public void run() {
            for (final ShowPlayerEntry entry : this.next) {
                final Player player = entry.getPlayer();
                final Player target = entry.getTarget();
                if ((player != null) && player.isOnline() && (target != null) && target.isOnline()) {
                    player.showPlayer(target);
                }
            }
            this.next.clear();
            this.next.addAll(this.entries);
            this.entries.clear();
        }
    }

    private final VanishPlugin plugin;
    private final Set<String> vanishedPlayerNames = Collections.synchronizedSet(new HashSet<String>());
    private final Map<String, Boolean> sleepIgnored = new HashMap<String, Boolean>();
    private final Set<UUID> bats = new HashSet<UUID>();
    private final VanishAnnounceManipulator announceManipulator;
    private final ShowPlayerHandler showPlayer = new ShowPlayerHandler();

    public VanishManager(final VanishPlugin plugin) {
        this.plugin = plugin;
        this.announceManipulator = new VanishAnnounceManipulator(this.plugin);
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this.showPlayer, 4, 4);

        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, this.plugin.getName() + ":vanishStatus", new PluginMessageListener() {
            @Override
            public void onPluginMessageReceived(String channel, Player player, byte[] message) {
                if (channel.equals(plugin.getName() + ":vanishStatus") && new String(message).equals("check")) {
                    player.sendPluginMessage(plugin, plugin.getName() + ":vanishStatus", VanishManager.this.isVanished(player) ? new byte[]{0x01} : new byte[]{0x00});
                }
            }
        });
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, this.plugin.getName() + "vanishStatus");

    }

    /**
     * Gets the announcement manipulator
     * Called by JSONAPI
     *
     * @return the Announce Manipulator
     */
    public VanishAnnounceManipulator getAnnounceManipulator() {
        return this.announceManipulator;
    }

    public Set<UUID> getBats() {
        return this.bats;
    }

    public Set<String> getVanishedPlayers() {
        return ImmutableSet.copyOf(this.vanishedPlayerNames);
    }

    /**
     * Gets if a player is vanished
     *
     * @param player player to query
     * @return true if vanished
     */
    public boolean isVanished(Player player) {
        return this.vanishedPlayerNames.contains(player.getName());
    }

    /**
     * Gets if a player is vanished
     *
     * @param playerName name of the player to query
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
     * Gets the number of vanished players
     *
     * @return the number of players currently vanished
     */
    public int numVanished() {
        return this.vanishedPlayerNames.size();
    }

    /**
     * Marks a player as having quit the game
     * Do not call this method
     *
     * @param player the player who has quit
     */
    public void playerQuit(Player player) {
        Debuggle.log("Quitting: " + player.getName());
        this.resetSleepingIgnored(player);
        VanishPerms.userQuit(player);
        this.removeVanished(player.getName());
        for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            otherPlayer.showPlayer(player);
        }
    }

    /**
     * Resets a player's visibility status based on current permissions
     *
     * @param player player to refresh
     */
    public void playerRefresh(Player player) {
        this.resetSeeing(player);
        if (this.isVanished(player) && !VanishPerms.canVanish(player)) {
            this.toggleVanish(player);
        }
    }

    /**
     * Forces a visibility refresh on a player
     *
     * @param player player to refresh
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
     * Toggles a player's visibility
     * Called when a player calls /vanish
     * Talks to the player and everyone with vanish.see
     * Will trigger effects
     *
     * @param togglingPlayer the player disappearing
     */
    public void toggleVanish(Player togglingPlayer) {
        this.toggleVanishQuiet(togglingPlayer);
        final String vanishingPlayerName = togglingPlayer.getName();
        final String messageBit;
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
     * Toggles a player's visibility
     * Does not say anything.
     *
     * @param vanishingPlayer
     */
    public void toggleVanishQuiet(Player vanishingPlayer) {
        final boolean vanishing = !this.isVanished(vanishingPlayer);
        final String vanishingPlayerName = vanishingPlayer.getName();
        if (vanishing) {
            Debuggle.log("It's invisible time! " + vanishingPlayer.getName());
            this.setSleepingIgnored(vanishingPlayer);
            if (VanishPerms.canNotFollow(vanishingPlayer)) {
                for (final Entity entity : vanishingPlayer.getNearbyEntities(100, 100, 100)) {
                    if ((entity != null) && (entity instanceof Creature)) {
                        final Creature creature = ((Creature) entity);
                        if ((creature != null) && (creature.getTarget() != null) && creature.getTarget().equals(vanishingPlayer)) {
                            creature.setTarget(null);
                        }
                    }
                }
            }
            this.vanishedPlayerNames.add(vanishingPlayerName);
            MetricsOverlord.getVanishTracker().increment();
            this.plugin.getLogger().info(vanishingPlayerName + " disappeared.");
        } else {
            Debuggle.log("It's visible time! " + vanishingPlayer.getName());
            this.resetSleepingIgnored(vanishingPlayer);
            this.removeVanished(vanishingPlayerName);
            MetricsOverlord.getUnvanishTracker().increment();
            this.plugin.getLogger().info(vanishingPlayerName + " reappeared.");
        }
        this.plugin.getServer().getPluginManager().callEvent(new VanishStatusChangeEvent(vanishingPlayer, vanishing));
        vanishingPlayer.sendPluginMessage(this.plugin, "vanishStatus", vanishing ? new byte[]{0x01} : new byte[]{0x00});
        final java.util.Collection<? extends Player> playerList = this.plugin.getServer().getOnlinePlayers();
        for (final Player otherPlayer : playerList) {
            if (vanishingPlayer.equals(otherPlayer)) {
                continue;
            }
            Debuggle.log("Determining what to do about " + vanishingPlayer.getName() + " for " + otherPlayer.getName());
            if (vanishing) {
                if (!VanishPerms.canSeeAll(otherPlayer)) {
                    if (otherPlayer.canSee(vanishingPlayer)) {
                        Debuggle.log("Hiding " + vanishingPlayer.getName() + " from " + otherPlayer.getName());
                        otherPlayer.hidePlayer(vanishingPlayer);
                    }
                } else {
                    otherPlayer.hidePlayer(vanishingPlayer);
                    this.showPlayer.add(new ShowPlayerEntry(otherPlayer, vanishingPlayer));
                }
            } else {
                if (VanishPerms.canSeeAll(otherPlayer)) {
                    otherPlayer.hidePlayer(vanishingPlayer);
                }
                if (!otherPlayer.canSee(vanishingPlayer)) {
                    Debuggle.log("Showing " + vanishingPlayer.getName() + " to " + otherPlayer.getName());
                    this.showPlayer.add(new ShowPlayerEntry(otherPlayer, vanishingPlayer));
                }
            }
        }
    }

    /**
     * Vanishes a player. Poof.
     * This is a convenience method.
     *
     * @param vanishingPlayer player to hide
     * @param silent if true, does not say anything
     * @param effects if true, trigger effects
     */
    public void vanish(Player vanishingPlayer, boolean silent, boolean effects) {
        if (this.isVanished(vanishingPlayer)) {
            return;
        }
        this.toggleVanish(vanishingPlayer);
    }

    /**
     * Reveals a player.
     * This is a convenience method.
     *
     * @param revealingPlayer player to reveal
     * @param silent if true, does not say anything
     * @param effects if true, trigger effects
     */
    public void reveal(Player revealingPlayer, boolean silent, boolean effects) {
        if (!this.isVanished(revealingPlayer)) {
            return;
        }
        this.toggleVanish(revealingPlayer);
    }

    private void hideVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (!player.equals(otherPlayer) && this.isVanished(otherPlayer) && player.canSee(otherPlayer)) {
                player.hidePlayer(otherPlayer);
            }
        }
    }

    private void removeVanished(String name) {
        this.vanishedPlayerNames.remove(name);
    }

    private void showVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.isVanished(otherPlayer) && !player.canSee(otherPlayer)) {
                this.showPlayer.add(new ShowPlayerEntry(player, otherPlayer));
            }
        }
    }

    void onPluginDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            for (final Player player2 : this.plugin.getServer().getOnlinePlayers()) {
                if ((player != null) && (player2 != null) && !player.equals(player2)) {
                    player.showPlayer(player2);
                }
            }
        }
    }

    void resetSleepingIgnored(Player player) {
        if (this.sleepIgnored.containsKey(player.getName())) {
            player.setSleepingIgnored(this.sleepIgnored.remove(player.getName()));
        }
    }

    void setSleepingIgnored(Player player) {
        if (!this.sleepIgnored.containsKey(player.getName())) {
            this.sleepIgnored.put(player.getName(), player.isSleepingIgnored());
        }
        player.setSleepingIgnored(true);
    }
}