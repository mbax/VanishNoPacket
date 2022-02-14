/*
 * VanishNoPacket
 * Copyright (C) 2011-2022 Matt Baxter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.Settings;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public final class ListenPlayerMessages implements Listener {
    private final VanishPlugin plugin;

    public ListenPlayerMessages(@NonNull VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(@NonNull AsyncPlayerChatEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotChat(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(@NonNull PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/me ") && this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotChat(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (Settings.getEnablePermTest()) {
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
                        message.append("Cannot find player: ").append(ChatColor.AQUA).append(split[1]);
                        event.getPlayer().sendMessage(message.toString());
                        event.setCancelled(true);
                        return;
                    }
                    message.append("Player ").append(ChatColor.AQUA).append(target.getName()).append(ChatColor.DARK_AQUA);
                    permission = split[2];
                    if (!target.hasPermission(permission)) {
                        message.append(" does not have ");
                    } else {
                        message.append(" has ");
                    }
                } else {
                    return;
                }
                event.setCancelled(true);
                message.append(ChatColor.AQUA).append(permission);
                event.getPlayer().sendMessage(message.toString());
            }

        }
    }
}
