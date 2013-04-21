package org.kitteh.vanish.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public final class ListenToYourHeart implements Listener {
    private final VanishPlugin plugin;

    public ListenToYourHeart(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void badReview(HangingBreakEvent criticism) {
        if (criticism instanceof HangingBreakByEntityEvent) {
            final Entity critic = ((HangingBreakByEntityEvent) criticism).getRemover();
            if (critic instanceof Player) {
                final Player arrogantFool = (Player) critic;
                if (this.plugin.getManager().isVanished(arrogantFool) && VanishPerms.canNotInteract(arrogantFool)) {
                    criticism.setCancelled(true);
                }
            }
        }
    }
}