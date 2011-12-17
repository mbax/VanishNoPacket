package to.joe.vanish.listeners;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import to.joe.vanish.VanishPerms;
import to.joe.vanish.VanishPlugin;

public class ListenPlayerJoinEarly extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayerJoinEarly(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.plugin.joinVanished() &&
                VanishPerms.silentJoin(event.getPlayer())) {
            this.plugin.getManager().toggleVanishQuiet(event.getPlayer());
            this.plugin.hooksVanish(event.getPlayer());
        }
    }
}
