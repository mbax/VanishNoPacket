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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishManager;

import java.util.Iterator;
import java.util.Set;

public final class ListenServerPing implements Listener {
    private final VanishManager manager;

    public ListenServerPing(@NonNull VanishManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void ping(@NonNull ServerListPingEvent event) {
        final Set<String> invisibles = this.manager.getVanishedPlayers();
        final Iterator<Player> players;
        try {
            players = event.iterator();
        } catch (final UnsupportedOperationException e) {
            return;
            // NOOP
        }
        Player player;
        while (players.hasNext()) {
            player = players.next();
            if (invisibles.contains(player.getName())) {
                players.remove();
            }
        }
    }
}
