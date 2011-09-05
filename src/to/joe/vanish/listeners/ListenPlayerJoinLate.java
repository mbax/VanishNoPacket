package to.joe.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import to.joe.vanish.VanishPerms;
import to.joe.vanish.VanishPlugin;

public class ListenPlayerJoinLate extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayerJoinLate(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (VanishPerms.silentJoin(event.getPlayer())) {
            this.plugin.getManager().getAnnounceManipulator().addToDelayedAnnounce(event.getPlayer().getName());
            event.setJoinMessage(null);
            String add = "";
            if (VanishPerms.canVanish(event.getPlayer())) {
                add = " To appear: /vanish";
            }
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You have joined invisibly." + add);
            this.plugin.messageSeers(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has joined vanished");
        }
    }
}
