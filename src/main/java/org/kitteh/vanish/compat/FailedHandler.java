package org.kitteh.vanish.compat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kitteh.vanish.compat.api.NMSCallProvider;

public final class FailedHandler implements NMSCallProvider {
    @Override
    public void sendEntityDestroy(Player player, int entityId) {
        player.sendMessage(ChatColor.AQUA + "VanishNoPacket needs an update.");
        player.sendMessage(ChatColor.AQUA + "Please log out then back in to fix your user");
    }

    @Override
    public void sendExplosionPacket(Location loc, Player subject) {
        if (subject != null) {
            subject.sendMessage(ChatColor.AQUA + "VanishNoPacket needs an update.");
            subject.sendMessage(ChatColor.AQUA + "Your explosion was not nearly as awesome without it");
        }
    }
}