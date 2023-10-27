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

import net.pl3x.map.core.Pl3xMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public final class Pl3xMapHook extends Hook {
    private boolean enabled = false;

    public Pl3xMapHook(final @NonNull VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Pl3xMap");
        if (grab != null && grab.isEnabled()) {
            this.plugin.getLogger().info("Now hooking into Pl3xMap");
            this.enabled = true;
        } else {
            this.plugin.getLogger().info("You wanted Pl3xMap support. I could not find Pl3xMap.");
            this.enabled = false;
        }
    }

    @Override
    public void onVanish(final @NonNull Player player) {
        if (this.enabled) {
            Pl3xMap.api().getPlayerRegistry()
                    .optional(player.getUniqueId())
                    .ifPresent(p -> p.setHidden(true, false));
        }
    }

    @Override
    public void onUnvanish(final @NonNull Player player) {
        if (this.enabled) {
            Pl3xMap.api().getPlayerRegistry()
                    .optional(player.getUniqueId())
                    .ifPresent(p -> p.setHidden(false, false));
        }
    }
}
