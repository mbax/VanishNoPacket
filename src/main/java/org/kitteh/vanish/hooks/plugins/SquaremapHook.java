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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;
import xyz.jpenilla.squaremap.api.Squaremap;

public final class SquaremapHook extends Hook {
    private boolean enabled = false;
    private Squaremap squaremap;

    public SquaremapHook(final @NonNull VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final @Nullable RegisteredServiceProvider<Squaremap> registration = this.plugin.getServer().getServicesManager().getRegistration(Squaremap.class);
        final @Nullable Squaremap instance = registration == null ? null : registration.getProvider();
        if (instance != null) {
            this.squaremap = instance;
            this.plugin.getLogger().info("Now hooking into squaremap");
            this.enabled = true;
        } else {
            this.plugin.getLogger().info("You wanted squaremap support. I could not find squaremap.");
        }
    }

    @Override
    public void onVanish(final @NonNull Player player) {
        if (this.enabled) {
            this.squaremap.playerManager().hide(player.getUniqueId());
        }
    }

    @Override
    public void onUnvanish(final @NonNull Player player) {
        if (this.enabled) {
            this.squaremap.playerManager().show(player.getUniqueId());
        }
    }
}
