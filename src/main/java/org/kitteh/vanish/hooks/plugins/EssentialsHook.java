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

import com.earth2me.essentials.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public final class EssentialsHook extends Hook {
    private final VanishPlugin plugin;
    private IEssentials essentials;

    public EssentialsHook(@NonNull VanishPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && this.plugin.getManager().isVanished(player)) {
                this.onUnvanish(player);
            }
        }
        this.essentials = null;
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (grab != null && grab.isEnabled()) {
            this.essentials = ((IEssentials) grab);
            this.plugin.getLogger().info("Now hooking into Essentials");
        } else {
            this.plugin.getLogger().info("You wanted Essentials support. I could not find Essentials.");
            this.essentials = null;
        }
    }

    @Override
    public void onUnvanish(@NonNull Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, false);
        }
    }

    @Override
    public void onVanish(@NonNull Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, true);
        }
    }

    private void setHidden(@NonNull Player player, boolean hide) {
        if (this.essentials != null) {
            this.essentials.getUser(player).setHidden(hide);
        }
    }
}
