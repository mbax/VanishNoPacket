package to.joe.vanish.listeners;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
        final Player player = event.getPlayer();
        this.plugin.getManager().safelist29Mod(((CraftPlayer) player).getEntityId(), this.plugin.getServer().getOnlinePlayers().length);
        this.plugin.getManager().removeVanished(player);
        this.plugin.hooksUnvanish(player);
        this.plugin.getManager().getAnnounceManipulator().delayedAnnounceKill(player.getName());
        if (this.plugin.getManager().getAnnounceManipulator().onQuitDoUsPart(player.getName())) {
            event.setQuitMessage(null);
        }
    }
}
