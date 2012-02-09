package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.metrics.MetricsOverlord;

public class ListenPlayerJoin implements Listener {

    private final VanishPlugin plugin;

    public ListenPlayerJoin(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEarly(PlayerJoinEvent event) {
        if (VanishPerms.joinVanished(event.getPlayer())) {
            this.plugin.getManager().toggleVanishQuiet(event.getPlayer());
            this.plugin.hooksVanish(event.getPlayer());
        }
        this.plugin.hooksJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinLate(PlayerJoinEvent event) {
        if (VanishPerms.joinVanished(event.getPlayer())) {
            String add = "";
            if (VanishPerms.canVanish(event.getPlayer())) {
                add = " To appear: /vanish";
            }
            MetricsOverlord.joininvis.increment();
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You have joined vanished." + add);
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has joined vanished");
        }
        if (VanishPerms.joinWithoutAnnounce(event.getPlayer())) {
            this.plugin.getManager().getAnnounceManipulator().addToDelayedAnnounce(event.getPlayer().getName());
            event.setJoinMessage(null);
        }
        if (VanishPerms.canReceiveAdminAlerts(event.getPlayer()) && this.plugin.versionDifference()) {
            event.getPlayer().sendMessage(ChatColor.AQUA + "[Vanish] This is version " + ChatColor.DARK_AQUA + this.plugin.getCurrentVersion() + ChatColor.AQUA + ", latest is " + ChatColor.DARK_AQUA + this.plugin.getLatestKnownVersion());
            event.getPlayer().sendMessage(ChatColor.AQUA + "[Vanish] Check " + ChatColor.DARK_AQUA + "http://dev.bukkit.org/server-mods/vanish/");
        }
        this.plugin.getManager().resetSeeing(event.getPlayer());
    }
}
