package org.kitteh.vanish.compat.v1_7_R2;

import java.util.ArrayList;

import net.minecraft.server.v1_7_R2.Block;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R2.PacketPlayOutExplosion;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.kitteh.vanish.compat.api.NMSCallProvider;

public final class NMSHandler implements NMSCallProvider {
    @Override
    public void sendExplosionPacket(Location loc, Player subject) {
        for (final Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distance(loc) < 256) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutExplosion(loc.getX(), loc.getY(), loc.getZ(), 3, new ArrayList<Block>(), null));
            }
        }
    }

    @Override
    public void sendEntityDestroy(Player player, int entityId) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
    }
}