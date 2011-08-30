package to.joe.vanish;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenPlayer extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayer(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && Perms.canNotPickUp(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getManager().removeVanished(event.getPlayer());
        if (this.plugin.getManager().hasLoginLineStored(event.getPlayer().getName())) {
            this.plugin.messageSeers(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has quit, still vanished");
            event.setQuitMessage(null);
        }
    }
}
