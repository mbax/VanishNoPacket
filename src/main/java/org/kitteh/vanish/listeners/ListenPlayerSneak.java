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
package org.kitteh.vanish.listeners;

import it.unimi.dsi.fastutil.Hash;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishCheck;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ListenShift implements Listener {
    private final VanishPlugin plugin;
    private final HashMap<UUID, Long> playersAndLastTimeSneaked;

    public ListenShift(@NonNull VanishPlugin instance) {
        this.plugin = instance;
        playersAndLastTimeSneaked = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerShift(@NonNull PlayerToggleSneakEvent event) {
        if(event.isSneaking()){
            if (this.plugin.getManager().isVanished(event.getPlayer())) {
                Player player = event.getPlayer();
                if(playersAndLastTimeSneaked.containsKey(player.getUniqueId())){
                    long lastTime = playersAndLastTimeSneaked.get(player.getUniqueId());
                    if(System.currentTimeMillis() - lastTime < 500){
                        player.sendMessage(ChatColor.GREEN + "Gamemode changed!");
                        if(player.getGameMode() == GameMode.SPECTATOR){
                            player.setGameMode(GameMode.CREATIVE);
                        }else{
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                        playersAndLastTimeSneaked.remove(player.getUniqueId());
                        return;
                    }
                }

                playersAndLastTimeSneaked.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

}
