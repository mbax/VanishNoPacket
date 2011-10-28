package to.joe.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import to.joe.vanish.VanishPerms;
import to.joe.vanish.VanishPlugin;

public class ListenPlayerCommandPreProcess extends PlayerListener {

    private final VanishPlugin plugin;
    private boolean enabled = false;

    public ListenPlayerCommandPreProcess(VanishPlugin instance) {
        this.plugin = instance;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (this.enabled) {
            final String[] split = event.getMessage().split(" ");
            if ((split.length > 1) && split[0].equalsIgnoreCase("/permtest")) {
                final boolean selfTest = VanishPerms.permTestSelf(event.getPlayer());
                final boolean otherTest = VanishPerms.permTestOther(event.getPlayer());
                if (!selfTest && !otherTest) {
                    return;
                }
                final StringBuilder message = new StringBuilder();
                String permission;
                message.append(ChatColor.DARK_AQUA);
                if ((split.length == 2) && selfTest) {
                    permission = split[1];
                    message.append("You");
                    if (!event.getPlayer().hasPermission(permission)) {
                        message.append(" do not");
                    }
                    message.append(" have ");
                } else if ((split.length == 3) && otherTest) {
                    final Player target = this.plugin.getServer().getPlayer(split[1]);
                    if (target == null) {
                        message.append("Cannot find player: " + ChatColor.AQUA + split[1]);
                        event.getPlayer().sendMessage(message.toString());
                        event.setCancelled(true);
                        return;
                    }
                    message.append("Player " + ChatColor.AQUA + target.getName() + ChatColor.DARK_AQUA);
                    permission = split[2];
                    if (!event.getPlayer().hasPermission(permission)) {
                        message.append(" does not have ");
                    } else {
                        message.append(" has ");
                    }
                } else {
                    return;
                }
                event.setCancelled(true);
                message.append(ChatColor.AQUA + permission);
                event.getPlayer().sendMessage(message.toString());
            }

        }
    }
}
