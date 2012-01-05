package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;


public class ListenPlayerMessagesSent extends PlayerListener {

    private final VanishPlugin plugin;
    private boolean permTestEnabled = false;

    public ListenPlayerMessagesSent(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotChat(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/me ") && this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotChat(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (this.permTestEnabled) {
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

    public void setPermTestEnabled(boolean enabled) {
        this.permTestEnabled = enabled;
    }
}
