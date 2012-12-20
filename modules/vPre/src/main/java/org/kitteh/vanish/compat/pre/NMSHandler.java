package org.kitteh.vanish.compat.pre;

import java.util.ArrayList;

import net.minecraft.server.Block;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet60Explosion;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.kitteh.vanish.compat.api.NMSCallProvider;

public class NMSHandler implements NMSCallProvider {

    @Override
    public void sendExplosionPacket(Location loc) {
        for (final Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distance(loc) < 256) {
                ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet60Explosion(loc.getX(), loc.getY(), loc.getZ(), 3, new ArrayList<Block>(), null));
            }
        }
    }

    @Override
    public void sendEntityDestroy(Player player, int entityId) {
        ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(entityId));
    }

}
