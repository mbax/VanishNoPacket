/*
 * VanishNoPacket
 * Copyright (C) 2011-2021 Matt Baxter
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
package org.kitteh.vanish.hooks.plugins;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public class DiscordSRVHook extends Hook {
    private boolean enabled = false;
    private DiscordSRV discordsrv;

    public DiscordSRVHook(@NonNull VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.enabled = true;
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("DiscordSRV");
        if (grab != null) {
            this.discordsrv = ((DiscordSRV) grab);
            this.plugin.getLogger().info("Now hooking into DiscordSRV");
        } else {
            this.plugin.getLogger().info("You wanted DiscordSRV support. I could not find DiscordSRV.");
            this.discordsrv = null;
            this.enabled = false;
        }
    }

    @Override
    public void onFakeJoin(@NonNull Player player) {
        if (this.enabled && player.hasPermission("vanish.hooks.discordsrv.broadcastfakejoin")) {
            this.discordsrv.sendJoinMessage(player, "");
        }
    }

    @Override
    public void onFakeQuit(@NonNull Player player) {
        if (this.enabled && player.hasPermission("vanish.hooks.discordsrv.broadcastfakequit")) {
            this.discordsrv.sendLeaveMessage(player, "");
        }
    }
}
