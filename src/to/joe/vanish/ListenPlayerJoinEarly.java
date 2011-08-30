package to.joe.vanish;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ListenPlayerJoinEarly extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayerJoinEarly(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Perms.silentJoin(event.getPlayer())) {
            this.plugin.getManager().packetSending(event.getPlayer());
        }
    }
}
