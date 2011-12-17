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
        if (this.plugin.joinVanished() &&
                VanishPerms.silentJoin(event.getPlayer())) {
            this.plugin.getManager().getAnnounceManipulator().addToDelayedAnnounce(event.getPlayer().getName());
            event.setJoinMessage(null);
            String add = "";
            if (VanishPerms.canVanish(event.getPlayer())) {
                add = " To appear: /vanish";
            }
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You have joined invisibly." + add);
            this.plugin.messageUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has joined vanished");
        }
        if (VanishPerms.canReceiveAdminAlerts(event.getPlayer()) && this.plugin.versionDifference()) {
            event.getPlayer().sendMessage(ChatColor.AQUA + "[Vanish] This is version " + ChatColor.DARK_AQUA + this.plugin.getCurrentVersion() + ChatColor.AQUA + ", latest is " + ChatColor.DARK_AQUA + this.plugin.getLatestVersion());
            event.getPlayer().sendMessage(ChatColor.AQUA + "[Vanish] Check " + ChatColor.DARK_AQUA + "http://dev.bukkit.org/server-mods/vanish/");
        }
    }
}
