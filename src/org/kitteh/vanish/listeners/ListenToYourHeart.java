package org.kitteh.vanish.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public class ListenToYourHeart implements Listener {

    private final VanishPlugin plugin;

    public ListenToYourHeart(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void badReview(PaintingBreakEvent criticism) {
        if (criticism instanceof PaintingBreakByEntityEvent) {
            final Entity critic = ((PaintingBreakByEntityEvent) criticism).getRemover();
            if (critic instanceof Player) {
                final Player arrogantFool = (Player) critic;
                if (this.plugin.getManager().isVanished(arrogantFool) && VanishPerms.canNotInteract(arrogantFool)) {
                    criticism.setCancelled(true);
                }
            }
        }
    }
}
