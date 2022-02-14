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
package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;

@SuppressWarnings("EmptyMethod")
public abstract class Hook {
    protected final VanishPlugin plugin;

    public Hook(@NonNull VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void onDisable() {

    }

    public void onEnable() {

    }

    public void onJoin(@NonNull Player player) {

    }

    public void onQuit(@NonNull Player player) {

    }

    public void onUnvanish(@NonNull Player player) {

    }

    public void onVanish(@NonNull Player player) {

    }

    public void onFakeJoin(@NonNull Player player) {

    }

    public void onFakeQuit(@NonNull Player player) {

    }
}
