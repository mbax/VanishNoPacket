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
package org.kitteh.vanish.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.Settings;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ListenPlayerOther implements Listener {
    private final VanishPlugin plugin;
    private final NamespacedKey lastGameModeNamespacedKey;
    private final Map<UUID, Long> playersAndLastTimeSneaked = new HashMap<>();

    public ListenPlayerOther(@NonNull VanishPlugin instance) {
        this.plugin = instance;
        this.lastGameModeNamespacedKey = new NamespacedKey(instance, "LastGameMode");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(@NonNull PlayerBucketFillEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(@NonNull PlayerDropItemEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodChange(@NonNull FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof final Player player && this.plugin.getManager().isVanished(player) && VanishPerms.canNotHunger(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(@NonNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock() != null) && (event.getClickedBlock().getState() instanceof Container container) && !this.plugin.chestFakeInUse(player.getName()) && !player.isSneaking() && this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canReadChestsSilently(event.getPlayer())) {
            if (container instanceof EnderChest && this.plugin.getServer().getPluginManager().isPluginEnabled("EnderChestPlus") && VanishPerms.canNotInteract(player)) {
                event.setCancelled(true);
                return;
            }
            Inventory inventory;
            if (container.getInventory() instanceof DoubleChestInventory) {
                if (this.plugin.isPaper()) {
                    inventory = this.plugin.getServer().createInventory(player, 54, Component.text("Silently opened inventory"));
                } else {
                    //noinspection deprecation
                    inventory = this.plugin.getServer().createInventory(player, 54, "Silently opened inventory");
                }
            } else {
                if (this.plugin.isPaper()) {
                    inventory = this.plugin.getServer().createInventory(player, container.getInventory().getType(), Component.text("Silently opened inventory"));
                } else {
                    //noinspection deprecation
                    inventory = this.plugin.getServer().createInventory(player, container.getInventory().getType(), "Silently opened inventory");
                }
            }
            inventory.setContents(container.getInventory().getContents());
            this.plugin.chestFakeOpen(player.getName());
            player.sendMessage(ChatColor.AQUA + "[VNP] Opening chest silently. Can not edit.");
            player.openInventory(inventory);
            event.setCancelled(true);
        } else if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotInteract(player)) {
            event.setCancelled(true);
        } else if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock() != null) && (event.getClickedBlock().getType() == Material.FARMLAND) && this.plugin.getManager().isVanished(player) && VanishPerms.canNotTrample(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(@NonNull EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && this.plugin.getManager().isVanished(player) && VanishPerms.canNotPickUp(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupArrow(@NonNull PlayerPickupArrowEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotPickUp(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(@NonNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (this.plugin.getManager().isVanished(player)) {
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has quit vanished");
        }
        this.plugin.getManager().playerQuit(player);
        this.plugin.hooksQuit(player);
        this.plugin.getManager().getAnnounceManipulator().dropDelayedAnnounce(player.getName());
        if (!this.plugin.getManager().getAnnounceManipulator().playerHasQuit(player.getName()) || VanishPerms.silentQuit(player)) {
            if (this.plugin.isPaper()) {
                event.quitMessage(null);
            } else {
                //noinspection deprecation
                event.setQuitMessage(null);
            }
        }
        this.plugin.chestFakeClose(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(@NonNull PlayerInteractEntityEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(@NonNull PlayerShearEntityEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NonNull PlayerChangedWorldEvent event) {
        if (Settings.getWorldChangeCheck()) {
            this.plugin.getManager().playerRefresh(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRaidTrigger(@NonNull RaidTriggerEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockForm(@NonNull EntityBlockFormEvent event) {
        if ((event.getEntity() instanceof Player player) && this.plugin.getManager().isVanished(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerShift(@NonNull PlayerToggleSneakEvent event) {
        if (!event.isSneaking() || !Settings.isDoubleSneakDuringVanishSwitchesGameMode() || !this.plugin.getManager().isVanished(event.getPlayer())) {
            return;
        }
        final Player player = event.getPlayer();
        final long now = System.currentTimeMillis();
        final long lastTime = this.playersAndLastTimeSneaked.computeIfAbsent(player.getUniqueId(), u -> now);
        if ((now != lastTime) && (now - lastTime < Settings.getDoubleSneakDuringVanishSwitchesGameModeTimeBetweenSneaksInMS())) {
            if (!Settings.getDoubleSneakDuringVanishSwitchesGameModeMessage().isBlank()) { //In case the user doesn't want a message to be sent at all
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Settings.getDoubleSneakDuringVanishSwitchesGameModeMessage()));
            }
            final PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.valueOf(persistentDataContainer.getOrDefault(this.lastGameModeNamespacedKey, PersistentDataType.STRING, "CREATIVE")));
            } else {
                persistentDataContainer.set(this.lastGameModeNamespacedKey, PersistentDataType.STRING, player.getGameMode().name());
                player.setGameMode(GameMode.SPECTATOR);
            }
            this.playersAndLastTimeSneaked.remove(player.getUniqueId());
        }
    }
}
