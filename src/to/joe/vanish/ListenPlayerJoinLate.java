package to.joe.vanish;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ListenPlayerJoinLate extends PlayerListener {

    private final VanishPlugin plugin;

    public ListenPlayerJoinLate(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Perms.silentJoin(event.getPlayer())) {
            this.plugin.getManager().addLoginLine(event.getPlayer().getName(), event.getJoinMessage());
            event.setJoinMessage(null);
            String add = "";
            if (Perms.canVanish(event.getPlayer())) {
                add = " To appear: /vanish";
            }
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You have joined invisibly." + add);
            this.plugin.messageSeers(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has joined vanished");
        }
    }
}
