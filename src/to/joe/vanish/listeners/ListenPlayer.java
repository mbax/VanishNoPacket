package to.joe.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import to.joe.vanish.VanishPerms;
import to.joe.vanish.VanishPlugin;

public class ListenPlayer extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayer(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotPickUp(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getManager().removeVanished(event.getPlayer());
        if (this.plugin.getManager().getAnnounceManipulator().delayedAnnounceKill(event.getPlayer().getName())) {
            this.plugin.messageUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has quit, still vanished");
            event.setQuitMessage(null);
        }
    }
}
