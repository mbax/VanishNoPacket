package org.kitteh.vanish.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public class ListenEntity implements Listener {

    private final VanishPlugin plugin;

    public ListenEntity(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        final Entity smacked = event.getEntity();
        if (smacked instanceof Player) {
            final Player player = (Player) smacked;
            if (this.plugin.getManager().isVanished(player) && VanishPerms.blockIncomingDamage(player)) {
                event.setCancelled(true);
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            final Entity damager = ev.getDamager();
            if (damager instanceof Player) {
                final Player player = (Player) damager;
                if (this.plugin.getManager().isVanished(player) && VanishPerms.blockOutgoingDamage(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getTarget()) && VanishPerms.canNotFollow((Player) event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

}
