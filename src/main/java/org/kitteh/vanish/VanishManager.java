/*
 * VanishNoPacket
 * Copyright (C) 2011-2021 Matt Baxter
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

import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class VanishManager {
    private static final class ShowPlayerEntry {
        private final Player player;
        private final Player target;

        public ShowPlayerEntry(@NonNull Player player, @NonNull Player target) {
            this.player = player;
            this.target = target;
        }

        public @NonNull Player getPlayer() {
            return this.player;
        }

        public @NonNull Player getTarget() {
            return this.target;
        }
    }

    private static final class ShowPlayerHandler implements Runnable {
        private final VanishPlugin plugin;
        private final Set<ShowPlayerEntry> entries = new HashSet<>();
        private final Set<ShowPlayerEntry> next = new HashSet<>();

        private ShowPlayerHandler(VanishPlugin plugin) {
            this.plugin = plugin;
        }

        public void add(@NonNull ShowPlayerEntry player) {
            this.entries.add(player);
        }

        @Override
        public void run() {
            for (final ShowPlayerEntry entry : this.next) {
                final Player player = entry.getPlayer();
                final Player target = entry.getTarget();
                if (player.isOnline() && target.isOnline()) {
                    player.showPlayer(this.plugin, target);
                }
            }
            this.next.clear();
            this.next.addAll(this.entries);
            this.entries.clear();
        }
    }

    public static final String VANISH_PLUGIN_CHANNEL = "vanishnopacket:status";

    private final VanishPlugin plugin;
    private final Set<String> vanishedPlayerNames = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Boolean> sleepIgnored = new HashMap<>();
    private final Set<UUID> bats = new HashSet<>();
    private final VanishAnnounceManipulator announceManipulator;
    private final Random random = new Random();
    private final ShowPlayerHandler showPlayer;
    private final NamespacedKey vanishCollideState;

    public VanishManager(final @NonNull VanishPlugin plugin) {
        this.plugin = plugin;

        this.announceManipulator = new VanishAnnounceManipulator(this.plugin);

        this.showPlayer = new ShowPlayerHandler(this.plugin);
        this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this.showPlayer, 4, 4);

        this.vanishCollideState = new NamespacedKey(this.plugin, "collidable");

        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, VanishManager.VANISH_PLUGIN_CHANNEL, (channel, player, message) -> {
            if (channel.equals(VanishManager.VANISH_PLUGIN_CHANNEL) && new String(message).equals("check")) {
                player.sendPluginMessage(plugin, VanishManager.VANISH_PLUGIN_CHANNEL, VanishManager.this.isVanished(player) ? new byte[]{0x01} : new byte[]{0x00});
            }
        });
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, VanishManager.VANISH_PLUGIN_CHANNEL);
    }

    /**
     * Gets the announcement manipulator
     *
     * @return the Announce Manipulator
     */
    public @NonNull VanishAnnounceManipulator getAnnounceManipulator() {
        return this.announceManipulator;
    }

    public @NonNull Set<UUID> getBats() {
        return this.bats;
    }

    public @NonNull Set<String> getVanishedPlayers() {
        return ImmutableSet.copyOf(this.vanishedPlayerNames);
    }

    /**
     * Gets if a player is vanished
     *
     * @param player player to query
     * @return true if vanished
     */
    public boolean isVanished(@NonNull Player player) {
        return this.vanishedPlayerNames.contains(player.getName());
    }

    /**
     * Gets if a player is vanished
     *
     * @param playerName name of the player to query
     * @return if the named player is currently vanished
     */
    public boolean isVanished(@NonNull String playerName) {
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
    public void playerQuit(@NonNull Player player) {
        Debuggle.log("Quitting: " + player.getName());
        this.resetSleepingIgnored(player);
        VanishPerms.userQuit(player);
        this.removeVanished(player.getName());
        for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            otherPlayer.showPlayer(this.plugin, player);
        }
    }

    /**
     * Resets a player's visibility status based on current permissions
     *
     * @param player player to refresh
     */
    public void playerRefresh(@NonNull Player player) {
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
    public void resetSeeing(@NonNull Player player) {
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
    public void toggleVanish(@NonNull Player togglingPlayer) {
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
     * Will trigger effects
     * Called by toggleVanish(Player)
     *
     * @param vanishingPlayer vanishing player
     */
    public void toggleVanishQuiet(@NonNull Player vanishingPlayer) {
        this.toggleVanishQuiet(vanishingPlayer, true);
    }

    /**
     * Toggles a player's visibility
     * Does not say anything.
     *
     * @param vanishingPlayer vanishing player
     * @param effects if true, trigger effects
     */
    public void toggleVanishQuiet(@NonNull Player vanishingPlayer, boolean effects) {
        final boolean vanishing = !this.isVanished(vanishingPlayer);
        final String vanishingPlayerName = vanishingPlayer.getName();
        if (vanishing) {
            Debuggle.log("It's invisible time! " + vanishingPlayer.getName());
            this.setSleepingIgnored(vanishingPlayer);
            if (VanishPerms.canNotFollow(vanishingPlayer)) {
                for (final Entity entity : vanishingPlayer.getNearbyEntities(100, 100, 100)) {
                    if (entity instanceof Creature) {
                        final Creature creature = ((Creature) entity);
                        if (creature.getTarget() != null && creature.getTarget().equals(vanishingPlayer)) {
                            creature.setTarget(null);
                        }
                    }
                }
            }
            vanishingPlayer.getPersistentDataContainer().set(this.vanishCollideState, PersistentDataType.BYTE, (byte) (vanishingPlayer.isCollidable() ? 0x01 : 0x00));
            if (vanishingPlayer.isCollidable()) {
                vanishingPlayer.setCollidable(false);
            }
            this.vanishedPlayerNames.add(vanishingPlayerName);
            this.plugin.getLogger().info(vanishingPlayerName + " disappeared.");
        } else {
            Debuggle.log("It's visible time! " + vanishingPlayer.getName());
            this.resetSleepingIgnored(vanishingPlayer);
            this.removeVanished(vanishingPlayerName);
            byte coll = vanishingPlayer.getPersistentDataContainer().getOrDefault(this.vanishCollideState, PersistentDataType.BYTE, (byte) 0x00);
            if (coll == 0x01) {
                vanishingPlayer.setCollidable(true);
            }
            this.plugin.getLogger().info(vanishingPlayerName + " reappeared.");
        }
        if (effects) {
            final Location oneUp = vanishingPlayer.getLocation().add(0, 1, 0);
            if (VanishPerms.canEffectSmoke(vanishingPlayer)) {
                this.effectSmoke(vanishingPlayer.getLocation());
            }
            if (VanishPerms.canEffectExplode(vanishingPlayer)) {
                this.effectExplosion(vanishingPlayer);
            }
            if (VanishPerms.canEffectLightning(vanishingPlayer)) {
                this.effectLightning(vanishingPlayer.getLocation());
            }
            if (VanishPerms.canEffectFlames(vanishingPlayer)) {
                this.effectFlames(oneUp);
            }
            if (VanishPerms.canEffectBats(vanishingPlayer)) {
                this.effectBats(oneUp);
            }
        }
        this.plugin.getServer().getPluginManager().callEvent(new VanishStatusChangeEvent(vanishingPlayer, vanishing));
        vanishingPlayer.sendPluginMessage(this.plugin, VanishManager.VANISH_PLUGIN_CHANNEL, vanishing ? new byte[]{0x01} : new byte[]{0x00});
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
                        otherPlayer.hidePlayer(this.plugin, vanishingPlayer);
                    }
                } else {
                    otherPlayer.hidePlayer(this.plugin, vanishingPlayer);
                    this.showPlayer.add(new ShowPlayerEntry(otherPlayer, vanishingPlayer));
                }
            } else {
                if (VanishPerms.canSeeAll(otherPlayer)) {
                    otherPlayer.hidePlayer(this.plugin, vanishingPlayer);
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
    public void vanish(@NonNull Player vanishingPlayer, boolean silent, boolean effects) {
        if (this.isVanished(vanishingPlayer)) {
            return;
        }
        if (silent) {
            this.toggleVanishQuiet(vanishingPlayer, effects);
        } else {
            this.toggleVanish(vanishingPlayer);
        }
    }

    /**
     * Reveals a player.
     * This is a convenience method.
     *
     * @param revealingPlayer player to reveal
     * @param silent if true, does not say anything
     * @param effects if true, trigger effects
     */
    public void reveal(@NonNull Player revealingPlayer, boolean silent, boolean effects) {
        if (!this.isVanished(revealingPlayer)) {
            return;
        }
        if (silent) {
            this.toggleVanishQuiet(revealingPlayer, effects);
        } else {
            this.toggleVanish(revealingPlayer);
        }
    }

    private void effectBats(final @NonNull Location location) {
        final Set<UUID> batty = new HashSet<>();
        for (int x = 0; x < 10; x++) {
            batty.add(location.getWorld().spawnEntity(location, EntityType.BAT).getUniqueId());
        }
        this.bats.addAll(batty);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            VanishManager.this.effectBatsCleanup(location.getWorld(), batty);
            VanishManager.this.bats.removeAll(batty);
        }, 3 * 20);
    }

    private void effectBatsCleanup(@NonNull World world, @NonNull Set<UUID> bats) {
        for (final Entity entity : world.getEntities()) {
            if (bats.contains(entity.getUniqueId())) {
                world.playEffect(entity.getLocation(), Effect.SMOKE, this.random.nextInt(9));
                entity.remove();
            }
        }
    }

    private void effectExplosion(@NonNull Player player) {
        Location loc = player.getLocation();
        player.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 0F, false, false);
    }

    private void effectFlames(@NonNull Location location) {
        for (int i = 0; i < 10; i++) {
            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, this.random.nextInt(9));
        }
    }

    private void effectLightning(@NonNull Location location) {
        final int x = location.getBlockX();
        final double y = location.getBlockY();
        final int z = location.getBlockZ();
        for (int i = 0; i < Settings.getLightningCount(); i++) {
            double xToStrike;
            double zToStrike;
            if (this.random.nextBoolean()) {
                xToStrike = x + this.random.nextInt(6);
            } else {
                xToStrike = x - this.random.nextInt(6);
            }
            if (this.random.nextBoolean()) {
                zToStrike = z + this.random.nextInt(6);
            } else {
                zToStrike = z - this.random.nextInt(6);
            }
            final Location toStrike = new Location(location.getWorld(), xToStrike, y, zToStrike);
            location.getWorld().strikeLightningEffect(toStrike);
        }
    }

    private void effectSmoke(@NonNull Location location) {
        for (int i = 0; i < 10; i++) {
            location.getWorld().playEffect(location, Effect.SMOKE, this.random.nextInt(9));
        }
    }

    private void hideVanished(@NonNull Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (!player.equals(otherPlayer) && this.isVanished(otherPlayer) && player.canSee(otherPlayer)) {
                player.hidePlayer(this.plugin, otherPlayer);
            }
        }
    }

    private void removeVanished(@NonNull String name) {
        this.vanishedPlayerNames.remove(name);
    }

    private void showVanished(@NonNull Player player) {
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
                    player.showPlayer(this.plugin, player2);
                }
            }
        }
        for (final World world : this.plugin.getServer().getWorlds()) {
            this.effectBatsCleanup(world, this.bats);
        }
    }

    void resetSleepingIgnored(@NonNull Player player) {
        if (this.sleepIgnored.containsKey(player.getName())) {
            player.setSleepingIgnored(this.sleepIgnored.remove(player.getName()));
        }
    }

    void setSleepingIgnored(@NonNull Player player) {
        if (!this.sleepIgnored.containsKey(player.getName())) {
            this.sleepIgnored.put(player.getName(), player.isSleepingIgnored());
        }
        player.setSleepingIgnored(true);
    }
}
