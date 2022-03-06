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

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.Settings;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public final class ListenPlayerMessages implements Listener {
    private final VanishPlugin plugin;
    private Outdated outdated;

    public ListenPlayerMessages(@NonNull VanishPlugin instance) {
        this.plugin = instance;
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners().length > 0) {
                    ListenPlayerMessages.this.plugin.getServer().getPluginManager().registerEvents(ListenPlayerMessages.this.outdated = new Outdated(ListenPlayerMessages.this.plugin), ListenPlayerMessages.this.plugin);
                }
            }
        }.runTaskLater(this.plugin, 1);
    }

    public static final class Outdated implements Listener {
        private final VanishPlugin plugin;

        public Outdated(@NonNull VanishPlugin instance) {
            this.plugin = instance;
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerChat(@SuppressWarnings("deprecation") @NonNull AsyncPlayerChatEvent event) {
            if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotChat(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(@NonNull AsyncChatEvent event) {
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
                String permission;
                if ((split.length == 2) && selfTest) {
                    permission = split[1];
                    final StringBuilder message = new StringBuilder();
                    message.append("You");
                    if (!event.getPlayer().hasPermission(permission)) {
                        message.append(" do not");
                    }
                    message.append(" have ");
                    event.getPlayer().sendMessage(Component.text().color(Settings.getDark()).content(message.toString()).append(Component.text().color(Settings.getLight()).content(permission)));
                } else if ((split.length == 3) && otherTest) {
                    final Player target = this.plugin.getServer().getPlayer(split[1]);
                    if (target == null) {
                        event.getPlayer().sendMessage(Component.text().color(Settings.getDark()).content("Cannot find player: ").append(Component.text().color(Settings.getLight()).content(split[1])));
                    } else {
                        permission = split[2];
                        event.getPlayer().sendMessage(
                                Component.text().color(Settings.getDark()).content("Player ")
                                        .append(Component.text().color(Settings.getLight()).content(target.getName()))
                                        .append(Component.text(target.hasPermission(permission) ? " has " : " does not have "))
                                        .append(Component.text().color(Settings.getLight()).content(permission))
                        );
                    }
                } else {
                    return;
                }
                event.setCancelled(true);
            }

        }
    }
}
