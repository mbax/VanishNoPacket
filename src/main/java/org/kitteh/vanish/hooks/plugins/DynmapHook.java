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
package org.kitteh.vanish.hooks.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dynmap.DynmapAPI;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public final class DynmapHook extends Hook {
    private DynmapAPI dynmap;
    private boolean enabled = false;

    public DynmapHook(@NonNull VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && this.plugin.getManager().isVanished(player)) {
                this.onUnvanish(player);
            }
        }
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("dynmap");
        if (grab != null && grab.isEnabled()) {
            this.dynmap = ((DynmapAPI) grab);
            this.plugin.getLogger().info("Now hooking into Dynmap");
            this.enabled = true;
        } else {
            this.plugin.getLogger().info("You wanted Dynmap support. I could not find Dynmap.");
            this.dynmap = null;
            this.enabled = false;
        }
    }

    @Override
    public void onJoin(@NonNull Player player) {
        if (player.hasPermission("vanish.hooks.dynmap.alwayshidden")) {
            this.onVanish(player);
        }
    }

    @Override
    public void onUnvanish(@NonNull Player player) {
        if (this.enabled && (this.dynmap != null) && !player.hasPermission("vanish.hooks.dynmap.alwayshidden")) {
            this.dynmap.assertPlayerInvisibility(player.getName(), false, "VanishNoPacket");
        }
    }

    @Override
    public void onVanish(@NonNull Player player) {
        if (this.enabled && (this.dynmap != null)) {
            this.dynmap.assertPlayerInvisibility(player.getName(), true, "VanishNoPacket");
        }
    }
}
