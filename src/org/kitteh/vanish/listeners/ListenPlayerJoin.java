package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.kitteh.vanish.Messages;
import org.kitteh.vanish.VanishCheck;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.metrics.MetricsOverlord;

public class ListenPlayerJoin implements Listener {

    private final VanishPlugin plugin;

    public ListenPlayerJoin(VanishPlugin instance) {
        this.plugin = instance;
    }

    /**
     * Handle setting who the player can see and setting invisibility if needed
     * here.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEarly(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("vanished", new LazyMetadataValue(this.plugin, CacheStrategy.NEVER_CACHE, new VanishCheck(this.plugin.getManager(), event.getPlayer().getName())));
        this.plugin.getManager().resetSeeing(event.getPlayer());
        if (VanishPerms.joinVanished(event.getPlayer())) {
            MetricsOverlord.joininvis.increment();
            this.plugin.getManager().toggleVanishQuiet(event.getPlayer());
            this.plugin.hooksVanish(event.getPlayer());
        }
        this.plugin.hooksJoin(event.getPlayer());
    }

    /**
     * Handle any messages to the player here
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinLate(PlayerJoinEvent event) {
        if (VanishPerms.joinVanished(event.getPlayer())) {
            String add = "";
            if (VanishPerms.canVanish(event.getPlayer())) {
                add = Messages.getString("ListenPlayerJoin.ToAppear");
            }
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + Messages.getString("ListenPlayerJoin.JoinedVanished") + add);
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + Messages.getString("ListenPlayerJoin.OtherPlayerJoinedVanished"));
        }
        if (VanishPerms.joinWithoutAnnounce(event.getPlayer())) {
            this.plugin.getManager().getAnnounceManipulator().addToDelayedAnnounce(event.getPlayer().getName());
            event.setJoinMessage(null);
        }
        if (VanishPerms.canReceiveAdminAlerts(event.getPlayer()) && this.plugin.versionDifference()) {
            event.getPlayer().sendMessage(ChatColor.AQUA + Messages.getString("ListenPlayerJoin.CurrentVersion") + ChatColor.DARK_AQUA + this.plugin.getCurrentVersion() + ChatColor.AQUA + Messages.getString("ListenPlayerJoin.LatestVersion") + ChatColor.DARK_AQUA + this.plugin.getLatestKnownVersion());
            event.getPlayer().sendMessage(ChatColor.AQUA + Messages.getString("ListenPlayerJoin.CheckDBO") + ChatColor.DARK_AQUA + "http://dev.bukkit.org/server-mods/vanish/");
        }
    }
}
