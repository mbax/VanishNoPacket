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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public final class ListenEntity implements Listener {
    private final VanishPlugin plugin;

    public ListenEntity(@NonNull VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NonNull EntityDamageEvent event) {
        final Entity smacked = event.getEntity();
        if (this.plugin.getManager().getBats().contains(smacked.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (smacked instanceof final Player player && this.plugin.getManager().isVanished(player) && VanishPerms.blockIncomingDamage(player)) {
            event.setCancelled(true);
        }
        if (event instanceof final EntityDamageByEntityEvent ev) {
            final Entity damager = ev.getDamager();
            Player player = null;
            if (damager instanceof Player) {
                player = (Player) damager;
            } else if (damager instanceof final Projectile projectile) {
                if ((projectile.getShooter() != null) && (projectile.getShooter() instanceof Player)) {
                    player = (Player) projectile.getShooter();
                }
            }
            if ((player != null) && this.plugin.getManager().isVanished(player) && VanishPerms.blockOutgoingDamage(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(@NonNull EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player player) && this.plugin.getManager().isVanished(player) && VanishPerms.canNotFollow(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(@NonNull VehicleDestroyEvent event) {
        final Entity entity = event.getAttacker();
        if ((entity instanceof Player player) && this.plugin.getManager().isVanished(player) && VanishPerms.canNotInteract(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEntityCollision(@NonNull VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player player) && this.plugin.getManager().isVanished(player)) {
            event.setCancelled(true);
        }
    }
}
