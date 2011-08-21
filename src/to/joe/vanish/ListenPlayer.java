package to.joe.vanish;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenPlayer extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayer(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Perms.silentJoin(event.getPlayer())) {
            this.plugin.getManager().packetSending(event.getPlayer());
            this.plugin.getManager().addLoginLine(event.getPlayer().getName(), event.getJoinMessage());
            event.setJoinMessage(null);
            String add = "";
            if (Perms.canVanish(event.getPlayer())) {
                add = " To appear: /vanish";
            }
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You have joined invisibly." + add);
            this.plugin.messageSeers(ChatColor.DARK_AQUA+event.getPlayer().getName()+" has joined vanished");
        }
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
            this.plugin.messageSeers(event.getQuitMessage());
            event.setQuitMessage(null);
        }
    }

}
